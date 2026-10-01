import java.net.*;

/** Experiment 8: Sliding Window (Go-Back-N) - SENDER program over UDP.
 *  Keeps up to `window` unacknowledged frames in flight; a cumulative ACK slides the window forward;
 *  on timeout the whole window is retransmitted from the oldest unacknowledged frame.
 *  javac SlidingWindowSender.java && java SlidingWindowSender [host] [port] [frames] [window]
 *  (defaults: localhost 8000 10 4) */
public class SlidingWindowSender {
    public static void main(String[] args) throws Exception {
        InetAddress host = InetAddress.getByName(args.length > 0 ? args[0] : "localhost");
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 8000;
        int total = args.length > 2 ? Integer.parseInt(args[2]) : 10;
        int window = args.length > 3 ? Integer.parseInt(args[3]) : 4;
        int timeoutMs = 500;
        int base = 0, next = 0, sent = 0;

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(timeoutMs);
            System.out.println("Sender: " + total + " frames, window " + window);
            byte[] buf = new byte[64];
            while (base < total) {
                while (next < base + window && next < total) {          // fill the window
                    send(socket, host, port, next, total);
                    System.out.println("Sent Frame " + next + "   window [" + base + ", " + (base + window - 1) + "]");
                    next++;
                    sent++;
                }
                try {
                    DatagramPacket ack = new DatagramPacket(buf, buf.length);
                    socket.receive(ack);
                    int n = Integer.parseInt(new String(ack.getData(), 0, ack.getLength()));
                    if (n > base) {
                        System.out.println("Got ACK " + n + " -> window slides to start at " + n);
                        base = n;
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("TIMEOUT -> go back and resend from Frame " + base);
                    for (int i = base; i < next; i++) { send(socket, host, port, i, total); sent++; System.out.println("Resent Frame " + i); }
                }
            }
            System.out.println("All frames acknowledged. Transmissions: " + sent + ", retransmissions: " + (sent - total));
        }
    }

    static void send(DatagramSocket s, InetAddress host, int port, int seq, int total) throws Exception {
        byte[] d = (seq + "|" + total + "|message-" + seq).getBytes();
        s.send(new DatagramPacket(d, d.length, host, port));
    }
}
