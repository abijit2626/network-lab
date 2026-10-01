import java.io.*;
import java.net.*;

/** Experiment 3: TCP server in Java (echo, one client at a time).
 *  javac Exp03TcpServer.java && java Exp03TcpServer [port]    (default 5000) */
public class Exp03TcpServer {
    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 5000;
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("TCP server listening on port " + port);
            while (true) {
                try (Socket client = server.accept()) {
                    System.out.println("Client connected: " + client.getInetAddress().getHostAddress() + ":" + client.getPort());
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println("Received: " + line);
                        out.println("Server echo: " + line);
                        if (line.equalsIgnoreCase("bye")) break;
                    }
                    System.out.println("Client disconnected");
                }
            }
        }
    }
}
