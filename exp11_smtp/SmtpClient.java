import java.io.*;
import java.net.*;

/** Experiment 11: SMTP client on raw sockets.
 *  javac SmtpClient.java && java SmtpClient [host] [port] [from] [to] [subject] [body] */
public class SmtpClient {
    static BufferedReader in;
    static PrintWriter out;

    static int reply() throws IOException {
        String l;
        do { l = in.readLine(); System.out.println("S: " + l); } while (l.length() > 3 && l.charAt(3) == '-');
        return Integer.parseInt(l.substring(0, 3));
    }

    static void cmd(String c, int ok) throws IOException {
        System.out.println("C: " + c);
        out.print(c + "\r\n");
        out.flush();
        if (reply() != ok) throw new IOException("Unexpected reply to " + c);
    }

    public static void main(String[] a) throws Exception {
        String host = a.length > 0 ? a[0] : "localhost";
        int port = a.length > 1 ? Integer.parseInt(a[1]) : 2525;
        String from = a.length > 2 ? a[2] : "alice@lab.local", to = a.length > 3 ? a[3] : "bob@lab.local";
        String subject = a.length > 4 ? a[4] : "Test mail", body = a.length > 5 ? a[5] : "Hello from the SMTP lab client.";
        try (Socket s = new Socket(host, port)) {
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream());
            reply();
            cmd("HELO client.local", 250);
            cmd("MAIL FROM:<" + from + ">", 250);
            cmd("RCPT TO:<" + to + ">", 250);
            cmd("DATA", 354);
            out.print("From: " + from + "\r\nTo: " + to + "\r\nSubject: " + subject + "\r\n\r\n" + body.replace("\n.", "\n..") + "\r\n.\r\n");
            out.flush();
            reply();
            cmd("QUIT", 221);
        }
    }
}
