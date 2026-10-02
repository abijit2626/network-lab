import java.io.*;
import java.net.*;
import java.nio.file.*;

public class FtpServer {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) { System.out.println("usage: java FtpServer <file> [port]"); return; }
        Path file = Paths.get(args[0]);
        if (!Files.isRegularFile(file)) { System.out.println("File not found: " + file); return; }
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 2121;

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("FTP server ready on port " + port + ", waiting for a client to receive \"" + file.getFileName() + "\" ...");
            try (Socket client = server.accept()) {
                System.out.println("Client connected: " + client.getInetAddress().getHostAddress());
                byte[] data = Files.readAllBytes(file);
                DataOutputStream out = new DataOutputStream(client.getOutputStream());
                out.writeUTF(file.getFileName().toString());
                out.writeLong(data.length);
                out.write(data);
                out.flush();
                System.out.println("Sent " + data.length + " bytes.");
            }
        }
    }
}
