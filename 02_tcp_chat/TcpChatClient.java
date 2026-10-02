import java.io.*;
import java.net.*;

public class TcpChatClient {
    public static void main(String[] args) throws IOException {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5000;
        try (Socket socket = new Socket(host, port)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Thread reader = new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) System.out.println(line);
                } catch (IOException e) {  }
                System.out.println("[disconnected]");
                System.exit(0);
            });
            reader.setDaemon(true);
            reader.start();

            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            String msg;
            while ((msg = keyboard.readLine()) != null) {
                out.println(msg);
                if (msg.equalsIgnoreCase("bye")) break;
            }
        }
    }
}
