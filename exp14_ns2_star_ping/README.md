# Experiment 14 – Ping over a star topology (NS-2)
`ns star_ping.tcl 6` builds a hub (node 0) with 5 leaves on 0.1 Mb/10 ms links with a 3-packet queue.
All leaves ping each other in bursts, so the hub's queues overflow. The script reports the count of `d` (drop) events in `star.tr`.
Vary `n`, the link bandwidth, the queue limit or the ping interval to see how drops change.
`grep "^d" star.tr | awk '{print $5}' | sort | uniq -c` breaks drops down by packet type.
