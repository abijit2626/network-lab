import java.net.*;
import java.util.*;

/** Experiment 5: UDP chat server - registers clients by their first message ("JOIN name") and relays every
 *  message to all other clients. Type "bye" from a client to leave.
 *  javac UdpChatServer.java && java UdpChatServer [port]    (default 7000) */
public class UdpChatServer {
    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 7000;
        Map<SocketAddress, String> clients = new LinkedHashMap<>();
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("UDP chat server on port " + port);
            byte[] buf = new byte[1024];
            while (true) {
                DatagramPacket p = new DatagramPacket(buf, buf.length);
                socket.receive(p);
                String msg = new String(p.getData(), 0, p.getLength()).trim();
                SocketAddress from = p.getSocketAddress();
                String out;
                if (msg.startsWith("JOIN ")) {
                    clients.put(from, msg.substring(5));
                    out = "*** " + clients.get(from) + " joined the chat";
                } else if (!clients.containsKey(from)) {
                    continue;                                   // ignore datagrams from unregistered senders
                } else if (msg.equalsIgnoreCase("bye")) {
                    out = "*** " + clients.remove(from) + " left the chat";
                } else {
                    out = clients.get(from) + ": " + msg;
                }
                System.out.println(out);
                byte[] data = out.getBytes();
                for (SocketAddress c : clients.keySet())
                    if (!c.equals(from)) socket.send(new DatagramPacket(data, data.length, c));
            }
        }
    }
}
