import java.io.*;
import java.net.*;
import java.nio.file.*;

public class FtpClient {
    public static void main(String[] args) throws IOException {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 2121;

        try (Socket socket = new Socket(host, port)) {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            String name = Paths.get(in.readUTF()).getFileName().toString();
            long size = in.readLong();
            Path target = Paths.get("received_" + name);
            System.out.println("Receiving \"" + name + "\" (" + size + " bytes) ...");
            try (OutputStream out = Files.newOutputStream(target)) {
                byte[] buf = new byte[4096];
                long left = size;
                while (left > 0) {
                    int n = in.read(buf, 0, (int) Math.min(buf.length, left));
                    if (n < 0) throw new EOFException("connection closed early");
                    out.write(buf, 0, n);
                    left -= n;
                }
            }
            System.out.println("File saved as " + target.toAbsolutePath());
        }
    }
}
