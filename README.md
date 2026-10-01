# Computer Networks Lab (24CSCNWP508)
One file per experiment (experiment 2 has a server and a client file), all in `experiments/`. C and Java are used; NS-2 needs Tcl. Compile Java with `javac <file>` and run with `java <ClassName>`.

| # | Experiment | File | Run |
|---|---|---|---|
| 1 | Basic networking commands | `exp01_basic_commands.sh` | `./exp01_basic_commands.sh [host]` |
| 2 | Network system calls | `exp02_server.c`, `exp02_client.c` | `./server [port]` then `./client [ip] [port]` |
| 3 | IP, subnetting, supernetting | `Exp03Subnetting.java` | `java Exp03Subnetting 192.168.10.0/24` |
| 3 | TCP client/server in Java | `Exp03TcpServer.java`, `Exp03TcpClient.java` | `java Exp03TcpServer [port]` then `java Exp03TcpClient [host] [port]` |
| 4 | Network design (optional, Packet Tracer) | `exp04_network_design.md` | step-by-step guide |
| 4 | UDP chat: sender / receiver (Java) | `Exp04Sender.java`, `Exp04Receiver.java` | `java Exp04Receiver [port]` then `java Exp04Sender [host] [port]` |
| 5 | TCP client/server | `exp05_tcp_sockets.c` | `./tcp server [port]` then `./tcp client [ip] [port]` |
| 5 | UDP chat (multi-client) in Java | `Exp05UdpChatServer.java`, `Exp05UdpChatClient.java` | `java Exp05UdpChatServer [port]` then `java Exp05UdpChatClient <name> [host] [port]` |
| 6 | UDP client/server | `exp06_udp_sockets.c` | `./udp server [port]` then `./udp client [ip] [port]` |
| 7 | Stop-and-Wait, Stop-and-Wait ARQ | `Exp07StopAndWait.java` | `java Exp07StopAndWait 5 0.3` |
| 8 | Go-Back-N, Selective Repeat | `Exp08SlidingWindow.java` | `java Exp08SlidingWindow both 10 4 0.2` |
| 8 | Sliding window over UDP: sender & receiver | `Exp08SlidingWindowSender.java`, `Exp08SlidingWindowReceiver.java` | `java Exp08SlidingWindowReceiver [port] [loss]` then `java Exp08SlidingWindowSender [host] [port] [frames] [window]` |
| 9 | Distance Vector / Link State routing | `Exp09Routing.java` | `java Exp09Routing distancevector` or `linkstate` |
| 10 | Packet sniffer | `exp10_packet_sniffer.c` | `sudo ./sniffer 20 [port]` |
| 11 | SMTP | `Exp11Smtp.java` | `java Exp11Smtp smtpserver` then `java Exp11Smtp smtpclient` |
| 12 | FTP | `Exp12Ftp.java` | `java Exp12Ftp ftpserver` then `java Exp12Ftp ftpclient` (student/lab123) |
| 13 | NS-2 introduction | `exp13_ns2_intro.tcl` | `ns exp13_ns2_intro.tcl` |
| 14 | Ping over star topology (NS-2) | `exp14_ns2_star_ping.tcl` | `ns exp14_ns2_star_ping.tcl 6` |
| – | Leaky bucket traffic shaping | `LeakyBucket.java` | `java LeakyBucket` or `java LeakyBucket --input` |
