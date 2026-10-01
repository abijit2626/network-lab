import java.io.*;
import java.net.*;
import java.nio.file.*;

/** File transfer server: sends a text file to the client that connects.
 *  javac FtpServer.java && java FtpServer <file.txt> [port]      (default port 2121; start this first) */
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
                out.writeUTF(file.getFileName().toString());      // file name
                out.writeLong(data.length);                       // file size
                out.write(data);                                  // file contents
                out.flush();
                System.out.println("Sent " + data.length + " bytes.");
            }
        }
    }
}
