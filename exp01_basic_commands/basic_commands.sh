#!/bin/bash
# Experiment 1: Basic networking commands in Linux
# Usage: ./basic_commands.sh [host]   (default host: 8.8.8.8)
HOST=${1:-8.8.8.8}
run() { echo; echo "=================== $* ==================="; "$@" 2>&1 | head -40; }

run hostname
run hostname -I
run ip addr show            # ifconfig equivalent
run ip route show           # routing table (route -n / netstat -rn)
run ip neigh show           # ARP table (arp -a)
run ip -s link show         # interface statistics
run ping -c 4 "$HOST"       # ICMP reachability, RTT, packet loss
command -v traceroute >/dev/null && run traceroute -m 10 "$HOST" || echo "traceroute not installed"
command -v nslookup  >/dev/null && run nslookup example.com     || echo "nslookup not installed"
command -v dig       >/dev/null && run dig +short example.com   || echo "dig not installed"
command -v ss        >/dev/null && run ss -tuln                 # netstat -tuln equivalent
command -v netstat   >/dev/null && run netstat -i
command -v curl      >/dev/null && run curl -sI https://example.com
cat /etc/resolv.conf 2>/dev/null | head
