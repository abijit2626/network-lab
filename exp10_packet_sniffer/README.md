# Experiment 10 – Study of application protocols with packet sniffers
**A. Wireshark / tcpdump (recommended for the record)**
```
sudo tcpdump -i any -nn -c 50 port 53          # DNS
sudo tcpdump -i any -nn -A port 80             # HTTP (plain text)
sudo tcpdump -i lo -nn port 21 or port 25      # FTP / SMTP (run exp11/exp12 servers on those ports)
```
Wireshark display filters: `dns`, `http`, `ftp`, `smtp`, `tcp.flags.syn==1`, `icmp`. Observe: DNS query/response,
TCP 3-way handshake (SYN, SYN-ACK, ACK), HTTP GET/200 OK, FTP USER/PASS/RETR commands in clear text, SMTP HELO/MAIL FROM/RCPT TO/DATA.

**B. Own sniffer:** `sudo python3 sniffer.py -c 30 [-p 80]` decodes Ethernet/IP/TCP/UDP/ICMP headers and tags the application protocol.
Generate traffic in another terminal (`ping 8.8.8.8`, `curl http://example.com`, `nslookup example.com`).
