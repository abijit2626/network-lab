import java.io.*;
import java.net.*;

public class UdpChatClient {
    public static void main(String[] args) throws Exception {
        String name = args.length > 0 ? args[0] : "user" + (int) (Math.random() * 1000);
        InetAddress host = InetAddress.getByName(args.length > 1 ? args[1] : "localhost");
        int port = args.length > 2 ? Integer.parseInt(args[2]) : 7000;
        DatagramSocket socket = new DatagramSocket();

        Thread receiver = new Thread(() -> {
            byte[] buf = new byte[1024];
            try {
                while (!socket.isClosed()) {
                    DatagramPacket p = new DatagramPacket(buf, buf.length);
                    socket.receive(p);
                    System.out.println("\r" + new String(p.getData(), 0, p.getLength()));
                }
            } catch (IOException e) {  }
        });
        receiver.setDaemon(true);
        receiver.start();

        send(socket, "JOIN " + name, host, port);
        System.out.println("Joined as " + name + ". Type messages ('bye' to leave).");
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = keyboard.readLine()) != null) {
            send(socket, line, host, port);
            if (line.equalsIgnoreCase("bye")) break;
        }
        socket.close();
    }

    static void send(DatagramSocket s, String msg, InetAddress host, int port) throws IOException {
        byte[] d = msg.getBytes();
        s.send(new DatagramPacket(d, d.length, host, port));
    }
}
