import java.net.*;

/** Experiment 4: Receiver (UDP datagram socket).
 *  javac Exp04Receiver.java && java Exp04Receiver [port]    (default 6000; start this first) */
public class Exp04Receiver {
    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 6000;
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Receiver waiting on UDP port " + port);
            byte[] buf = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String msg = new String(packet.getData(), 0, packet.getLength());
                System.out.println("From " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " -> " + msg);
                byte[] ack = ("ACK: " + msg).getBytes();
                socket.send(new DatagramPacket(ack, ack.length, packet.getAddress(), packet.getPort()));
                if (msg.equalsIgnoreCase("bye")) break;
            }
        }
    }
}
