#!/usr/bin/env python3
"""Experiment 10: Packet sniffer / protocol analyser using a raw AF_PACKET socket (Linux, needs root).
Decodes Ethernet -> IPv4 -> TCP/UDP/ICMP and labels application protocols by port.
Usage: sudo python3 sniffer.py [-c count] [-p port]
"""
import argparse, socket, struct

APPS = {80: 'HTTP', 443: 'HTTPS', 21: 'FTP', 25: 'SMTP', 587: 'SMTP-submission', 53: 'DNS', 22: 'SSH',
        23: 'TELNET', 67: 'DHCP', 68: 'DHCP', 110: 'POP3', 143: 'IMAP'}
FLAGS = [(0x01, 'FIN'), (0x02, 'SYN'), (0x04, 'RST'), (0x08, 'PSH'), (0x10, 'ACK'), (0x20, 'URG')]


def mac(b): return ':'.join(f'{x:02x}' for x in b)


def parse(frame):
    dst, src, proto = struct.unpack('!6s6sH', frame[:14])
    out = [f"Ethernet {mac(src)} -> {mac(dst)} type=0x{proto:04x}"]
    if proto != 0x0800:
        return out, None
    ip = frame[14:]
    ihl = (ip[0] & 0x0F) * 4
    ttl, p, = ip[8], ip[9]
    s, d = socket.inet_ntoa(ip[12:16]), socket.inet_ntoa(ip[16:20])
    out.append(f"  IPv4 {s} -> {d} proto={p} ttl={ttl} len={struct.unpack('!H', ip[2:4])[0]}")
    t = ip[ihl:]
    ports = None
    payload = b''
    if p == 6 and len(t) >= 20:
        sp, dp, seq, ack, off, fl = struct.unpack('!HHLLBB', t[:14])
        off = (off >> 4) * 4
        flags = ','.join(n for b, n in FLAGS if fl & b)
        out.append(f"  TCP  {sp} -> {dp} seq={seq} ack={ack} flags=[{flags}]")
        ports, payload = (sp, dp), t[off:]
    elif p == 17 and len(t) >= 8:
        sp, dp, ln = struct.unpack('!HHH', t[:6])
        out.append(f"  UDP  {sp} -> {dp} len={ln}")
        ports, payload = (sp, dp), t[8:]
    elif p == 1:
        out.append(f"  ICMP type={t[0]} code={t[1]} ({'echo request' if t[0]==8 else 'echo reply' if t[0]==0 else 'other'})")
    if ports:
        app = APPS.get(ports[1]) or APPS.get(ports[0])
        if app:
            out.append(f"  APP  {app}")
            if app in ('HTTP', 'FTP', 'SMTP') and payload:
                out.append("       " + payload[:80].decode('ascii', 'replace').splitlines()[0] if payload.splitlines() else "")
    return out, ports


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('-c', type=int, default=20, help='packets to capture')
    ap.add_argument('-p', type=int, help='only show packets with this port')
    a = ap.parse_args()
    s = socket.socket(socket.AF_PACKET, socket.SOCK_RAW, socket.ntohs(3))
    n = 0
    while n < a.c:
        frame, _ = s.recvfrom(65535)
        lines, ports = parse(frame)
        if a.p and (not ports or a.p not in ports):
            continue
        n += 1
        print(f"--- packet {n} ({len(frame)} bytes)")
        print('\n'.join(l for l in lines if l))


if __name__ == '__main__':
    main()
