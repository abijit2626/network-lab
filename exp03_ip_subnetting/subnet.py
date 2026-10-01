#!/usr/bin/env python3
"""Experiment 3: IP addressing, subnetting, subnet masks, supernetting (no external libs)."""
import ipaddress, sys


def classify(ip):
    first = int(str(ip).split('.')[0])
    for lo, hi, c in ((1, 126, 'A'), (128, 191, 'B'), (192, 223, 'C'), (224, 239, 'D (multicast)'), (240, 255, 'E (reserved)')):
        if lo <= first <= hi:
            return c
    return 'loopback/special'


def describe(cidr):
    n = ipaddress.ip_network(cidr, strict=False)
    hosts = list(n.hosts())
    print(f"Network    : {n}")
    print(f"Class      : {classify(n.network_address)}")
    print(f"Mask       : {n.netmask}  (/{n.prefixlen})   Wildcard: {n.hostmask}")
    print(f"Network ID : {n.network_address}")
    print(f"Broadcast  : {n.broadcast_address}")
    print(f"Hosts      : {n.num_addresses - 2 if n.prefixlen < 31 else n.num_addresses} usable"
          + (f" ({hosts[0]} - {hosts[-1]})" if hosts else ""))
    print(f"Binary mask: {'.'.join(f'{int(o):08b}' for o in str(n.netmask).split('.'))}")


def subnet_by_count(cidr, count):
    n = ipaddress.ip_network(cidr, strict=False)
    bits = (count - 1).bit_length()
    print(f"\nSplitting {n} into >= {count} subnets (borrow {bits} bits -> /{n.prefixlen + bits}):")
    for i, s in enumerate(n.subnets(prefixlen_diff=bits), 1):
        h = list(s.hosts())
        print(f"  {i:>2}. {str(s):<18} {h[0]} - {h[-1]}  bcast {s.broadcast_address}")


def vlsm(cidr, host_reqs):
    n = ipaddress.ip_network(cidr, strict=False)
    cur = int(n.network_address)
    print(f"\nVLSM allocation inside {n}:")
    for h in sorted(host_reqs, reverse=True):
        bits = (h + 2 - 1).bit_length()
        net = ipaddress.ip_network((cur, 32 - bits))
        print(f"  need {h:>4} hosts -> {net}  (usable {net.num_addresses - 2})")
        cur += net.num_addresses


def supernet(cidrs):
    nets = [ipaddress.ip_network(c) for c in cidrs]
    sup = list(ipaddress.collapse_addresses(nets))
    print(f"\nSupernetting (route aggregation) of {', '.join(cidrs)}:")
    for s in sup:
        print(f"  -> {s}  mask {s.netmask}")


def same_subnet(ip1, ip2, mask):
    a = ipaddress.ip_interface(f"{ip1}/{mask}")
    b = ipaddress.ip_interface(f"{ip2}/{mask}")
    print(f"\n{ip1} and {ip2} with mask {mask}: " + ("SAME subnet" if a.network == b.network else "DIFFERENT subnets"))


if __name__ == '__main__':
    cidr = sys.argv[1] if len(sys.argv) > 1 else '192.168.10.0/24'
    describe(cidr)
    subnet_by_count(cidr, 4)
    vlsm(cidr, [100, 50, 20, 10])
    supernet(['192.168.0.0/24', '192.168.1.0/24', '192.168.2.0/24', '192.168.3.0/24'])
    same_subnet('192.168.10.5', '192.168.10.200', '255.255.255.128')
