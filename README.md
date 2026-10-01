# Computer Networks Lab (24CSCNWP508)
Programs for all experiments in the syllabus, written in C (system/socket level) and Java (protocols and simulations); NS-2 uses Tcl as required by the simulator. NS-2 scripts (13, 14) were not run here – NS-2 wasn't available; run them with `ns`. Experiment 4 is a GUI task (Packet Tracer), so it's a step-by-step guide.

| # | Experiment | Folder | Language | Run |
|---|---|---|---|---|
| 1 | Basic networking commands | `exp01_basic_commands` | Bash | `./basic_commands.sh` |
| 2 | Network system calls | `exp02_system_calls` | C | `gcc syscalls_demo.c -o sd && ./sd` |
| 3 | IP, subnetting, supernetting | `exp03_ip_subnetting` | Java | `javac Subnet.java && java Subnet 192.168.10.0/24` |
| 4 | Network design (optional) | `exp04_network_design` | Packet Tracer guide | – |
| 5 | TCP client/server | `exp05_tcp_sockets` | C | `./tcp_server` then `./tcp_client` |
| 6 | UDP client/server | `exp06_udp_sockets` | C | `./udp_server` then `./udp_client` |
| 7 | Stop-and-Wait, Stop-and-Wait ARQ | `exp07_stop_and_wait` | Java | `javac StopAndWait.java && java StopAndWait 5 0.3` |
| 8 | Sliding window: Go-Back-N, Selective Repeat | `exp08_sliding_window` | Java | `javac SlidingWindow.java && java SlidingWindow both 10 4 0.2` |
| 9 | Distance Vector & Link State routing | `exp09_routing` | Java | `java DistanceVector` / `java LinkState` (after `javac *.java`) |
| 10 | Packet sniffer / protocol analysis | `exp10_packet_sniffer` | C + Wireshark | `gcc sniffer.c -o sniffer && sudo ./sniffer 20` |
| 11 | SMTP | `exp11_smtp` | Java | `java SmtpServer` then `java SmtpClient` |
| 12 | FTP | `exp12_ftp` | Java | `java FtpServer` then `java FtpClient` (login student/lab123) |
| 13 | NS-2 introduction | `exp13_ns2_intro` | Tcl | `ns first.tcl` |
| 14 | Ping over star topology (NS-2) | `exp14_ns2_star_ping` | Tcl | `ns star_ping.tcl 6` |
