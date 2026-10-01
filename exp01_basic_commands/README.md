# Experiment 1 – Basic Networking Commands
| Command | Purpose |
|---|---|
| `ifconfig` / `ip addr` | Show/configure interfaces, IP, MAC, MTU |
| `ping host` | ICMP echo; tests reachability, RTT, packet loss |
| `traceroute host` | Shows each hop (uses TTL expiry) |
| `netstat -tuln` / `ss -tuln` | Listening sockets and connections |
| `nslookup` / `dig` | DNS queries |
| `arp -a` / `ip neigh` | IP→MAC cache |
| `route -n` / `ip route` | Routing table |
| `hostname`, `curl`, `tcpdump` | Host name, HTTP client, packet capture |

Run: `./basic_commands.sh [host]`
