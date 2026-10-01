# Computer Networks Lab (24CSCNWP508)
Programs for all experiments in the syllabus. NS-2 scripts (13, 14) were not run here – NS-2 wasn't available; run them with `ns`. Experiment 4 is a GUI task (Packet Tracer), so it's a step-by-step guide.

| # | Experiment | Folder | Language | Run |
|---|---|---|---|---|
| 1 | Basic networking commands | `exp01_basic_commands` | Bash | `./basic_commands.sh` |
| 2 | Network system calls | `exp02_system_calls` | C | `gcc syscalls_demo.c -o sd && ./sd` |
| 3 | IP, subnetting, supernetting | `exp03_ip_subnetting` | Python | `python3 subnet.py 192.168.10.0/24` |
| 4 | Network design (optional) | `exp04_network_design` | Packet Tracer guide | – |
| 5 | TCP client/server | `exp05_tcp_sockets` | C | `./tcp_server` then `./tcp_client` |
| 6 | UDP client/server | `exp06_udp_sockets` | C | `./udp_server` then `./udp_client` |
| 7 | Stop-and-Wait, Stop-and-Wait ARQ | `exp07_stop_and_wait` | Python | `python3 stop_and_wait.py 5 0.3` |
| 8 | Sliding window: Go-Back-N, Selective Repeat | `exp08_sliding_window` | Python | `python3 sliding_window.py both 10 4 0.2` |
| 9 | Distance Vector & Link State routing | `exp09_routing` | Python | `python3 distance_vector.py` |
| 10 | Packet sniffer / protocol analysis | `exp10_packet_sniffer` | Python + Wireshark | `sudo python3 sniffer.py -c 20` |
| 11 | SMTP | `exp11_smtp` | Python | `smtp_server.py` then `smtp_client.py` |
| 12 | FTP | `exp12_ftp` | Python | `ftp_server.py` then `ftp_client.py` (login student/lab123) |
| 13 | NS-2 introduction | `exp13_ns2_intro` | Tcl | `ns first.tcl` |
| 14 | Ping over star topology (NS-2) | `exp14_ns2_star_ping` | Tcl | `ns star_ping.tcl 6` |
