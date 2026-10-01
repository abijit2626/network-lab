import java.util.*;

/** Experiment 3: IP addressing, subnetting, subnet masks, supernetting.
 *  javac Subnet.java && java Subnet 192.168.10.0/24 */
public class Subnet {
    static long toLong(String ip) {
        long v = 0;
        for (String s : ip.split("\\.")) v = (v << 8) | Integer.parseInt(s);
        return v;
    }
    static String toIp(long v) {
        return ((v >> 24) & 255) + "." + ((v >> 16) & 255) + "." + ((v >> 8) & 255) + "." + (v & 255);
    }
    static long mask(int prefix) { return prefix == 0 ? 0 : (0xFFFFFFFFL << (32 - prefix)) & 0xFFFFFFFFL; }
    static String binary(long v) {
        StringBuilder sb = new StringBuilder();
        for (int i = 3; i >= 0; i--) {
            sb.append(String.format("%8s", Long.toBinaryString((v >> (8 * i)) & 255)).replace(' ', '0')).append(i > 0 ? "." : "");
        }
        return sb.toString();
    }
    static String classOf(long ip) {
        int f = (int) (ip >> 24);
        if (f < 128) return "A";
        if (f < 192) return "B";
        if (f < 224) return "C";
        return f < 240 ? "D (multicast)" : "E (reserved)";
    }

    static void describe(long ip, int prefix) {
        long m = mask(prefix), net = ip & m, bcast = net | (~m & 0xFFFFFFFFL);
        long hosts = prefix >= 31 ? (1L << (32 - prefix)) : (1L << (32 - prefix)) - 2;
        System.out.println("Network ID : " + toIp(net) + "/" + prefix);
        System.out.println("Class      : " + classOf(ip));
        System.out.println("Mask       : " + toIp(m) + "   Wildcard: " + toIp(~m & 0xFFFFFFFFL));
        System.out.println("Binary mask: " + binary(m));
        System.out.println("Broadcast  : " + toIp(bcast));
        System.out.println("Usable hosts: " + hosts + (hosts > 0 ? " (" + toIp(net + 1) + " - " + toIp(bcast - 1) + ")" : ""));
    }

    static void subnetByCount(long ip, int prefix, int count) {
        int bits = 32 - Integer.numberOfLeadingZeros(count - 1);
        int np = prefix + bits;
        long size = 1L << (32 - np), net = ip & mask(prefix);
        System.out.println("\nSplitting into >= " + count + " subnets (borrow " + bits + " bits -> /" + np + "):");
        for (int i = 0; i < (1 << bits); i++) {
            long s = net + i * size;
            System.out.printf("  %2d. %s/%d  %s - %s  bcast %s%n", i + 1, toIp(s), np, toIp(s + 1), toIp(s + size - 2), toIp(s + size - 1));
        }
    }

    static void vlsm(long ip, int prefix, Integer[] reqs) {
        Arrays.sort(reqs, Collections.reverseOrder());
        long cur = ip & mask(prefix);
        System.out.println("\nVLSM allocation:");
        for (int h : reqs) {
            int bits = 32 - Integer.numberOfLeadingZeros(h + 1);       // host bits needed for h+2 addresses
            long size = 1L << bits;
            System.out.printf("  need %4d hosts -> %s/%d (usable %d)%n", h, toIp(cur), 32 - bits, size - 2);
            cur += size;
        }
    }

    /** Supernetting: smallest common prefix covering all given networks. */
    static void supernet(String[] nets) {
        long first = toLong(nets[0].split("/")[0]);
        int prefix = 32;
        for (String n : nets) {
            long a = toLong(n.split("/")[0]);
            while (prefix > 0 && ((a ^ first) & mask(prefix)) != 0) prefix--;
        }
        System.out.println("\nSupernet of " + String.join(", ", nets) + ":\n  -> " + toIp(first & mask(prefix)) + "/" + prefix
                + "  mask " + toIp(mask(prefix)));
    }

    public static void main(String[] args) {
        String cidr = args.length > 0 ? args[0] : "192.168.10.0/24";
        long ip = toLong(cidr.split("/")[0]);
        int prefix = Integer.parseInt(cidr.split("/")[1]);
        describe(ip, prefix);
        subnetByCount(ip, prefix, 4);
        vlsm(ip, prefix, new Integer[]{100, 50, 20, 10});
        supernet(new String[]{"192.168.0.0/24", "192.168.1.0/24", "192.168.2.0/24", "192.168.3.0/24"});
        long a = toLong("192.168.10.5"), b = toLong("192.168.10.200"), m = toLong("255.255.255.128");
        System.out.println("\n192.168.10.5 and 192.168.10.200 with mask 255.255.255.128: "
                + ((a & m) == (b & m) ? "SAME subnet" : "DIFFERENT subnets"));
    }
}
