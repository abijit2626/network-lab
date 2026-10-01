import java.io.*;
import java.net.*;

/** Experiment 4: UDP chat - Sender side. Start the Receiver first, then type messages; replies appear as they arrive.
 *  Type "bye" to end the chat.
 *  javac Exp04Sender.java && java Exp04Sender [host] [port]    (default localhost 6000) */
public class Exp04Sender {
    public static void main(String[] args) throws Exception {
        InetAddress host = InetAddress.getByName(args.length > 0 ? args[0] : "localhost");
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 6000;
        DatagramSocket socket = new DatagramSocket();
        System.out.println("Sender chat to " + host.getHostAddress() + ":" + port + ". Type messages ('bye' to quit).");

        Thread reader = new Thread(() -> {
            byte[] b = new byte[1024];
            try {
                while (true) {
                    DatagramPacket p = new DatagramPacket(b, b.length);
                    socket.receive(p);
                    String m = new String(p.getData(), 0, p.getLength());
                    System.out.println("Receiver: " + m);
                    if (m.equalsIgnoreCase("bye")) { System.out.println("[Receiver left the chat]"); System.exit(0); }
                }
            } catch (IOException e) { /* socket closed */ }
        });
        reader.setDaemon(true);
        reader.start();

        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = keyboard.readLine()) != null) {
            byte[] d = line.getBytes();
            socket.send(new DatagramPacket(d, d.length, host, port));
            if (line.equalsIgnoreCase("bye")) break;
        }
        socket.close();
    }
}
