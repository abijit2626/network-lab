import java.util.Random;

/** Experiment 7: Stop-and-Wait and Stop-and-Wait ARQ on a lossy channel (simulation).
 *  javac StopAndWait.java && java StopAndWait [frames] [lossProb] [seed] */
public class StopAndWait {
    static void stopAndWait(int n) {
        System.out.println("\n--- Stop and Wait (ideal channel) ---");
        for (int i = 0; i < n; i++) {
            System.out.println("Sender  : send Frame " + i);
            System.out.println("Receiver: got Frame " + i + " -> send ACK " + i);
            System.out.println("Sender  : got ACK " + i);
        }
    }

    static void arq(int n, double loss, Random r) {
        System.out.println("\n--- Stop and Wait ARQ (loss probability " + loss + ") ---");
        int seq = 0, expected = 0, sent = 0, retx = 0;
        for (int i = 0; i < n; i++) {
            while (true) {
                sent++;
                System.out.println("Sender  : send Frame " + i + " (seq=" + seq + ")");
                if (r.nextDouble() < loss) {
                    System.out.println("Channel : Frame LOST");
                } else {
                    if (seq == expected) { System.out.println("Receiver: accepted Frame " + i); expected ^= 1; }
                    else System.out.println("Receiver: duplicate frame discarded");
                    if (r.nextDouble() < loss) System.out.println("Channel : ACK " + expected + " LOST");
                    else { System.out.println("Sender  : received ACK " + expected); seq ^= 1; break; }
                }
                System.out.println("Sender  : TIMEOUT -> retransmit");
                retx++;
            }
        }
        System.out.printf("%nFrames: %d  transmissions: %d  retransmissions: %d  efficiency: %.2f%%%n",
                n, sent, retx, 100.0 * n / sent);
    }

    public static void main(String[] a) {
        int n = a.length > 0 ? Integer.parseInt(a[0]) : 5;
        double p = a.length > 1 ? Double.parseDouble(a[1]) : 0.3;
        Random r = a.length > 2 ? new Random(Long.parseLong(a[2])) : new Random();
        stopAndWait(n);
        arq(n, p, r);
    }
}
