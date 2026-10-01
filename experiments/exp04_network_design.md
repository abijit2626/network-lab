# Experiment 4 (Optional) – Network design with multiple subnets & services
Done in Cisco Packet Tracer / GNS3 (GUI tool); steps and CLI are below. Services used: **DHCP, DNS, Web (HTTP), FTP**.

## Topology
```
 PC0,PC1 ─ Switch0 ─┐                       ┌─ Switch1 ─ Server0 (DNS/Web/FTP) 192.168.20.10
 (VLAN/LAN 192.168.10.0/24)  Router0 (G0/0 192.168.10.1, G0/1 192.168.20.1) ┘
 Wireless laptop ─ Access Point ─ Switch0
```
## Steps
1. Place 1 router (2911), 2 switches (2960), 1 access point, 1 server, 2 PCs, 1 wireless laptop. Connect with copper straight-through cables.
2. Router config:
```
enable
configure terminal
interface g0/0
 ip address 192.168.10.1 255.255.255.0
 no shutdown
interface g0/1
 ip address 192.168.20.1 255.255.255.0
 no shutdown
exit
ip dhcp excluded-address 192.168.10.1 192.168.10.10
ip dhcp pool LAN1
 network 192.168.10.0 255.255.255.0
 default-router 192.168.10.1
 dns-server 192.168.20.10
end
write memory
```
3. Server0 static IP 192.168.20.10/24, gateway 192.168.20.1. Services tab → enable **HTTP**, **FTP** (user cisco/cisco), **DNS** (add record `www.lab.local` → 192.168.20.10).
4. PCs: IP Configuration → DHCP. Wireless laptop: swap NIC for WPC300N and join the AP SSID.
5. Verify: `ping 192.168.20.10`, `nslookup www.lab.local`, browser → `http://www.lab.local`, `ftp 192.168.20.10`.
