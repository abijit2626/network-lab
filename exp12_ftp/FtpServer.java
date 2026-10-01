import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

/** Experiment 12: simplified FTP (RFC 959 subset) server - control + passive data connections.
 *  Commands: USER PASS PWD CWD PASV LIST RETR STOR QUIT. Root dir ./ftp_root, login student/lab123.
 *  javac FtpServer.java && java FtpServer [port]   (default 2121) */
public class FtpServer {
    static final Path ROOT = Paths.get("ftp_root").toAbsolutePath().normalize();
    static final Map<String, String> USERS = Map.of("student", "lab123", "anonymous", "");

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 2121;
        Files.createDirectories(ROOT);
        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("FTP server on port " + port + ", root=" + ROOT);
            while (true) {
                Socket c = ss.accept();
                new Thread(() -> { try { session(c); } catch (IOException e) { System.out.println(e); } }).start();
            }
        }
    }

    static Path resolve(Path cwd, String arg) {
        Path p = (arg.startsWith("/") ? ROOT.resolve(arg.substring(1)) : cwd.resolve(arg)).normalize();
        return p.startsWith(ROOT) ? p : null;                        // block ../ escapes
    }

    static void say(OutputStream os, String s) throws IOException {
        os.write((s + "\r\n").getBytes());
        os.flush();
    }

    static void session(Socket c) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
        OutputStream os = c.getOutputStream();
        say(os, "220 Lab FTP server ready");
        String user = null, line;
        boolean authed = false;
        Path cwd = ROOT;
        ServerSocket pasv = null;
        String host = c.getLocalAddress().getHostAddress();
        while ((line = in.readLine()) != null) {
            String[] p = line.trim().split(" ", 2);
            String cmd = p[0].toUpperCase(), arg = p.length > 1 ? p[1] : "";
            System.out.println("C: " + (cmd.equals("PASS") ? "PASS ****" : line));
            if (cmd.equals("USER")) { user = arg; say(os, "331 Password required"); }
            else if (cmd.equals("PASS")) {
                if (user != null && USERS.containsKey(user) && (USERS.get(user).isEmpty() || USERS.get(user).equals(arg))) { authed = true; say(os, "230 Login successful"); }
                else say(os, "530 Login incorrect");
            } else if (cmd.equals("QUIT")) { say(os, "221 Goodbye"); break; }
            else if (!authed) say(os, "530 Please login");
            else if (cmd.equals("PWD")) say(os, "257 \"/" + ROOT.relativize(cwd) + "\"");
            else if (cmd.equals("CWD")) {
                Path t = resolve(cwd, arg);
                if (t != null && Files.isDirectory(t)) { cwd = t; say(os, "250 OK"); } else say(os, "550 No such directory");
            } else if (cmd.equals("PASV")) {
                if (pasv != null) pasv.close();
                pasv = new ServerSocket(0, 1, InetAddress.getByName(host));
                int port = pasv.getLocalPort();
                say(os, "227 Entering Passive Mode (" + host.replace('.', ',') + "," + (port >> 8) + "," + (port & 255) + ")");
            } else if (cmd.equals("LIST") || cmd.equals("RETR") || cmd.equals("STOR")) {
                Path t = resolve(cwd, arg.isEmpty() ? "." : arg);
                if (t == null) { say(os, "550 Denied"); continue; }
                if ((cmd.equals("LIST") && !Files.isDirectory(t)) || (cmd.equals("RETR") && !Files.isRegularFile(t))) { say(os, "550 Not found"); continue; }
                if (pasv == null) { say(os, "425 Use PASV first"); continue; }
                say(os, "150 Opening data connection");
                try (Socket d = pasv.accept()) {
                    if (cmd.equals("LIST")) {
                        try (var st = Files.list(t)) {
                            for (Path f : (Iterable<Path>) st.sorted()::iterator)
                                d.getOutputStream().write(String.format("%s %10d %s\r\n", Files.isDirectory(f) ? "d" : "-", Files.size(f), f.getFileName()).getBytes());
                        }
                    } else if (cmd.equals("RETR")) Files.copy(t, d.getOutputStream());
                    else Files.copy(d.getInputStream(), t, StandardCopyOption.REPLACE_EXISTING);
                }
                pasv.close();
                pasv = null;
                say(os, "226 Transfer complete");
            } else say(os, "502 Command not implemented");
        }
        if (pasv != null) pasv.close();
        c.close();
    }
}
