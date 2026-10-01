import java.util.Random;

/** Experiment 8: Sliding window protocols - Go-Back-N and Selective Repeat (simulation).
 *  javac SlidingWindow.java && java SlidingWindow [gbn|sr|both] [frames] [window] [lossProb] [seed] */
public class SlidingWindow {
    static void goBackN(int total, int w, double loss, Random r) {
        System.out.println("Go-Back-N: " + total + " frames, window=" + w + ", loss=" + loss);
        int base = 0, next = 0, tx = 0;
        while (base < total) {
            while (next < base + w && next < total) { System.out.println("Sender  : send Frame " + next++); tx++; }
            int failed = -1;
            for (int f = base; f < next; f++) {
                if (r.nextDouble() < loss) { System.out.println("Channel : Frame " + f + " LOST"); failed = f; break; }
                System.out.println("Receiver: Frame " + f + " OK -> ACK " + f);
            }
            if (failed < 0) base = next;
            else {
                System.out.println("Receiver: out-of-order frames discarded; TIMEOUT -> go back to Frame " + failed);
                base = next = failed;
            }
        }
        System.out.println("Done. Transmissions=" + tx + ", retransmissions=" + (tx - total));
    }

    static void selectiveRepeat(int total, int w, double loss, Random r) {
        System.out.println("Selective Repeat: " + total + " frames, window=" + w + ", loss=" + loss);
        boolean[] acked = new boolean[total];
        int base = 0, tx = 0;
        while (base < total) {
            for (int f = base; f < Math.min(base + w, total); f++) {
                if (acked[f]) continue;
                System.out.println("Sender  : send Frame " + f);
                tx++;
                if (r.nextDouble() < loss) System.out.println("Channel : Frame " + f + " LOST");
                else { acked[f] = true; System.out.println("Receiver: Frame " + f + " buffered -> ACK " + f); }
            }
            while (base < total && acked[base]) System.out.println("Receiver: deliver Frame " + base++ + "; window slides");
            if (base < total) System.out.println("Sender  : TIMEOUT -> retransmit only un-ACKed frames");
        }
        System.out.println("Done. Transmissions=" + tx + ", retransmissions=" + (tx - total));
    }

    public static void main(String[] a) {
        String mode = a.length > 0 ? a[0] : "both";
        int total = a.length > 1 ? Integer.parseInt(a[1]) : 10;
        int w = a.length > 2 ? Integer.parseInt(a[2]) : 4;
        double loss = a.length > 3 ? Double.parseDouble(a[3]) : 0.2;
        Random r = a.length > 4 ? new Random(Long.parseLong(a[4])) : new Random();
        if (!mode.equals("sr")) { goBackN(total, w, loss, r); System.out.println(); }
        if (!mode.equals("gbn")) selectiveRepeat(total, w, loss, r);
    }
}
