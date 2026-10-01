import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

/** Experiment 11: minimal SMTP server (RFC 5321 subset); mails saved in ./mailbox.
 *  javac SmtpServer.java && java SmtpServer [port]   (default 2525) */
public class SmtpServer {
    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 2525;
        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("SMTP server on port " + port);
            while (true) {
                Socket s = ss.accept();
                new Thread(() -> { try { handle(s); } catch (IOException e) { System.out.println(e); } }).start();
            }
        }
    }

    static void say(OutputStream o, String x) throws IOException {
        o.write((x + "\r\n").getBytes());
        o.flush();
        System.out.println("S: " + x);
    }

    static void handle(Socket s) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        OutputStream out = s.getOutputStream();
        say(out, "220 labsmtp.local ESMTP ready");
        String sender = null, line;
        List<String> rcpts = new ArrayList<>();
        while ((line = in.readLine()) != null) {
            System.out.println("C: " + line);
            String u = line.toUpperCase();
            if (u.startsWith("HELO") || u.startsWith("EHLO")) say(out, "250 labsmtp.local Hello");
            else if (u.startsWith("MAIL FROM:")) { sender = line.substring(10).trim(); rcpts.clear(); say(out, "250 OK"); }
            else if (u.startsWith("RCPT TO:")) {
                if (sender == null) say(out, "503 Bad sequence of commands");
                else { rcpts.add(line.substring(8).trim()); say(out, "250 OK"); }
            } else if (u.equals("DATA")) {
                if (rcpts.isEmpty()) { say(out, "503 Need RCPT first"); continue; }
                say(out, "354 End data with <CR><LF>.<CR><LF>");
                StringBuilder body = new StringBuilder("X-Envelope-From: " + sender + "\nX-Envelope-To: " + rcpts + "\n");
                String l;
                while ((l = in.readLine()) != null && !l.equals(".")) body.append(l.startsWith("..") ? l.substring(1) : l).append('\n');
                Files.createDirectories(Paths.get("mailbox"));
                String name = "mailbox/" + System.currentTimeMillis() + ".eml";
                Files.writeString(Paths.get(name), body.toString());
                say(out, "250 OK message queued as " + name);
                sender = null;
                rcpts.clear();
            } else if (u.equals("RSET")) { sender = null; rcpts.clear(); say(out, "250 OK"); }
            else if (u.equals("NOOP")) say(out, "250 OK");
            else if (u.equals("QUIT")) { say(out, "221 Bye"); break; }
            else say(out, "502 Command not implemented");
        }
        s.close();
    }
}
