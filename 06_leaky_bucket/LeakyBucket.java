import java.util.*;

public class LeakyBucket {
    public static void main(String[] args) {
        int capacity = 10, rate = 3;
        int[] packets = {4, 8, 2, 6, 0, 9, 3, 0, 5};

        if (args.length > 0 && args[0].equals("--input")) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Bucket capacity (bytes): ");
            capacity = sc.nextInt();
            System.out.print("Output rate (bytes/sec): ");
            rate = sc.nextInt();
            System.out.print("Number of seconds / packets: ");
            int n = sc.nextInt();
            packets = new int[n];
            System.out.println("Enter packet size arriving at each second (0 for none):");
            for (int i = 0; i < n; i++) packets[i] = sc.nextInt();
        }

        System.out.println("Bucket capacity = " + capacity + ", output rate = " + rate + " bytes/sec\n");
        System.out.printf("%-6s %-9s %-10s %-9s %-9s %-8s%n", "Time", "Arrived", "Accepted", "Dropped", "Sent", "In bucket");
        int bucket = 0, totalSent = 0, totalDropped = 0;
        for (int t = 0; t < packets.length || bucket > 0; t++) {
            int arrived = t < packets.length ? packets[t] : 0, accepted = 0, dropped = 0;
            if (arrived > 0) {
                if (bucket + arrived <= capacity) { bucket += arrived; accepted = arrived; }
                else { dropped = arrived; totalDropped += arrived; }
            }
            int sent = Math.min(bucket, rate);
            bucket -= sent;
            totalSent += sent;
            System.out.printf("%-6d %-9d %-10d %-9d %-9d %-8d%n", t + 1, arrived, accepted, dropped, sent, bucket);
        }
        System.out.println("\nTotal sent = " + totalSent + " bytes, total dropped = " + totalDropped + " bytes");
    }
}
