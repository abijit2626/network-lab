import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class TcpChatServer {
    static final Map<String, PrintWriter> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 5000;
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("TCP chat server listening on port " + port);
            while (true) {
                Socket s = server.accept();
                new Thread(() -> handle(s)).start();
            }
        }
    }

    static void handle(Socket s) {
        String name = null;
        try (s) {
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            out.println("Enter your name:");
            name = in.readLine();
            if (name == null || name.isBlank() || clients.putIfAbsent(name, out) != null) {
                out.println("Name invalid or already taken.");
                name = null;
                return;
            }
            broadcast("*** " + name + " joined the chat", name);
            String line;
            while ((line = in.readLine()) != null) {
                if (line.equalsIgnoreCase("bye")) break;
                broadcast(name + ": " + line, name);
            }
        } catch (IOException e) {

        } finally {
            if (name != null) {
                clients.remove(name);
                broadcast("*** " + name + " left the chat", name);
            }
        }
    }

    static void broadcast(String msg, String except) {
        System.out.println(msg);
        clients.forEach((n, w) -> { if (!n.equals(except)) w.println(msg); });
    }
}
