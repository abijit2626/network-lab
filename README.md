# Computer Networks Lab
Java programs are compiled with `javac <files>` and run with `java <ClassName>`; C/C++ with `gcc` / `g++`.

| # | Experiment | Folder | Files | Run (server/receiver first) |
|---|---|---|---|---|
| 1 | Socket programming in C and C++ (TCP) | `01_socket_programming_c_cpp` | `server.c` `client.c` `server.cpp` `client.cpp` | `./server_c [port]` / `./client_c [ip] [port]` (same for `_cpp`) |
| 2 | TCP chat (Java) | `02_tcp_chat` | `TcpChatServer.java` `TcpChatClient.java` | `java TcpChatServer [port]` / `java TcpChatClient [host] [port]` |
| 3 | UDP chat (Java) | `03_udp_chat` | `UdpChatServer.java` `UdpChatClient.java` | `java UdpChatServer [port]` / `java UdpChatClient <name> [host] [port]` |
| 4 | Sliding window protocol (Go-Back-N, Java) | `04_sliding_window` | `SlidingWindowSender.java` `SlidingWindowReceiver.java` | `java SlidingWindowReceiver [port] [loss]` / `java SlidingWindowSender [host] [port] [frames] [window]` |
| 5 | Distance Vector Routing (Java) | `05_distance_vector_routing` | `DistanceVectorRouting.java` | `java DistanceVectorRouting [--input]` |
| 6 | Congestion control: leaky bucket (Java) | `06_leaky_bucket` | `LeakyBucket.java` | `java LeakyBucket [--input]` |
| 7 | FTP: server sends a .txt file, client receives it (Java) | `07_ftp` | `FtpServer.java` `FtpClient.java` | `java FtpServer <file.txt> [port]` / `java FtpClient [host] [port]` (saved as `received_<name>`) |
