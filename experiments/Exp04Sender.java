import java.io.*;
import java.net.*;

/** Experiment 4: Sender (UDP datagram socket).
 *  javac Exp04Sender.java && java Exp04Sender [host] [port]    (type 'bye' to quit) */
public class Exp04Sender {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 6000;
        InetAddress addr = InetAddress.getByName(host);
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(3000);                        // UDP is unreliable: don't wait forever for the ACK
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            String msg;
            while (System.out.printf("> ") != null && (msg = keyboard.readLine()) != null) {
                byte[] data = msg.getBytes();
                socket.send(new DatagramPacket(data, data.length, addr, port));
                byte[] buf = new byte[1024];
                DatagramPacket reply = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(reply);
                    System.out.println(new String(reply.getData(), 0, reply.getLength()));
                } catch (SocketTimeoutException e) {
                    System.out.println("timeout - no ACK received (datagram lost?)");
                }
                if (msg.equalsIgnoreCase("bye")) break;
            }
        }
    }
}
