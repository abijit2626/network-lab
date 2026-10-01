import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/** Experiment 12: interactive FTP client on sockets.
 *  javac FtpClient.java && java FtpClient [host] [port]
 *  Commands: ls, pwd, cd <dir>, get <file>, put <file>, quit */
public class FtpClient {
    static BufferedReader in;
    static PrintWriter out;

    static String reply() throws IOException { String l = in.readLine(); System.out.println("S: " + l); return l; }
    static String cmd(String c) throws IOException { out.print(c + "\r\n"); out.flush(); return reply(); }

    static Socket pasv() throws IOException {
        String r = cmd("PASV");
        Matcher m = Pattern.compile("\\((\\d+),(\\d+),(\\d+),(\\d+),(\\d+),(\\d+)\\)").matcher(r);
        if (!m.find()) throw new IOException("PASV failed");
        return new Socket(m.group(1) + "." + m.group(2) + "." + m.group(3) + "." + m.group(4),
                Integer.parseInt(m.group(5)) * 256 + Integer.parseInt(m.group(6)));
    }

    public static void main(String[] a) throws Exception {
        String host = a.length > 0 ? a[0] : "localhost";
        int port = a.length > 1 ? Integer.parseInt(a[1]) : 2121;
        Scanner sc = new Scanner(System.in);
        try (Socket s = new Socket(host, port)) {
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream());
            reply();
            System.out.print("user: ");
            cmd("USER " + sc.nextLine());
            System.out.print("password: ");
            cmd("PASS " + sc.nextLine());
            while (true) {
                System.out.print("ftp> ");
                String line = sc.hasNextLine() ? sc.nextLine().trim() : "quit";
                String[] p = line.split(" ", 2);
                String arg = p.length > 1 ? p[1] : "";
                switch (p[0]) {
                    case "ls" -> {
                        try (Socket d = pasv()) {
                            if (cmd("LIST " + arg).startsWith("150")) {
                                System.out.println(new String(d.getInputStream().readAllBytes()));
                                reply();
                            }
                        }
                    }
                    case "pwd" -> cmd("PWD");
                    case "cd" -> cmd("CWD " + arg);
                    case "get" -> {
                        try (Socket d = pasv()) {
                            if (cmd("RETR " + arg).startsWith("150")) {
                                Files.copy(d.getInputStream(), Paths.get(Paths.get(arg).getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
                                reply();
                            }
                        }
                    }
                    case "put" -> {
                        try (Socket d = pasv()) {
                            if (cmd("STOR " + Paths.get(arg).getFileName()).startsWith("150")) {
                                try (OutputStream o = d.getOutputStream()) { Files.copy(Paths.get(arg), o); }
                                reply();
                            }
                        }
                    }
                    case "quit" -> { cmd("QUIT"); return; }
                    default -> System.out.println("commands: ls pwd cd get put quit");
                }
            }
        }
    }
}
