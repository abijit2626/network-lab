import java.io.*;
import java.net.*;

/** Experiment 4: UDP chat - Receiver side (start this first). It waits for the Sender's first message, then both
 *  sides can type and read messages at the same time. Type "bye" to end the chat.
 *  javac Exp04Receiver.java && java Exp04Receiver [port]    (default 6000) */
public class Exp04Receiver {
    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 6000;
        DatagramSocket socket = new DatagramSocket(port);
        System.out.println("Receiver chat waiting on UDP port " + port + " ...");

        byte[] buf = new byte[1024];
        DatagramPacket first = new DatagramPacket(buf, buf.length);
        socket.receive(first);                                  // learn the sender's address from its first datagram
        InetAddress peer = first.getAddress();
        int peerPort = first.getPort();
        String msg = new String(first.getData(), 0, first.getLength());
        System.out.println("Sender: " + msg);
        if (msg.equalsIgnoreCase("bye")) { socket.close(); return; }

        Thread reader = new Thread(() -> {
            byte[] b = new byte[1024];
            try {
                while (true) {
                    DatagramPacket p = new DatagramPacket(b, b.length);
                    socket.receive(p);
                    String m = new String(p.getData(), 0, p.getLength());
                    System.out.println("Sender: " + m);
                    if (m.equalsIgnoreCase("bye")) { System.out.println("[Sender left the chat]"); System.exit(0); }
                }
            } catch (IOException e) { /* socket closed */ }
        });
        reader.setDaemon(true);
        reader.start();

        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = keyboard.readLine()) != null) {
            byte[] d = line.getBytes();
            socket.send(new DatagramPacket(d, d.length, peer, peerPort));
            if (line.equalsIgnoreCase("bye")) break;
        }
        socket.close();
    }
}
