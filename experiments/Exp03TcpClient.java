import java.io.*;
import java.net.*;

/** Experiment 3: TCP client in Java.
 *  javac Exp03TcpClient.java && java Exp03TcpClient [host] [port]   (type 'bye' to quit) */
public class Exp03TcpClient {
    public static void main(String[] args) throws IOException {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5000;
        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connected to " + host + ":" + port + ". Type messages ('bye' to quit)");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            String msg;
            while (System.out.printf("> ") != null && (msg = keyboard.readLine()) != null) {
                out.println(msg);
                System.out.println(in.readLine());
                if (msg.equalsIgnoreCase("bye")) break;
            }
        }
    }
}
