import java.net.*;
import java.util.Random;

public class SlidingWindowReceiver {
    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8000;
        double loss = args.length > 1 ? Double.parseDouble(args[1]) : 0.2;
        Random rnd = new Random();
        try (DatagramSocket socket = new DatagramSocket(port)) {
            socket.setSoTimeout(8000);
            System.out.println("Receiver (Go-Back-N) on UDP port " + port + ", frame loss " + loss);
            int expected = 0;
            byte[] buf = new byte[1024];
            try {
                while (true) {
                    DatagramPacket p = new DatagramPacket(buf, buf.length);
                    socket.receive(p);
                    String[] f = new String(p.getData(), 0, p.getLength()).split("\\|", 3);
                    int seq = Integer.parseInt(f[0]), total = Integer.parseInt(f[1]);
                    if (rnd.nextDouble() < loss) { System.out.println("Frame " + seq + " LOST in channel"); continue; }
                    if (seq == expected) {
                        System.out.println("Frame " + seq + " received in order: \"" + f[2] + "\" -> deliver");
                        expected++;
                    } else {
                        System.out.println("Frame " + seq + " out of order (expected " + expected + ") -> discarded");
                    }
                    byte[] ack = ("" + expected).getBytes();
                    socket.send(new DatagramPacket(ack, ack.length, p.getAddress(), p.getPort()));
                    System.out.println("   sent ACK " + expected);
                    if (expected == total) { System.out.println("All " + total + " frames received."); socket.setSoTimeout(2000); }
                }
            } catch (SocketTimeoutException e) {
                System.out.println("Receiver finished.");
            }
        }
    }
}
