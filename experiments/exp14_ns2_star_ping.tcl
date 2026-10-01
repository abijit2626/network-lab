# # Experiment 14 – Ping over a star topology (NS-2)
# `ns exp14_ns2_star_ping.tcl 6` builds a hub (node 0) with 5 leaves on 0.1 Mb/10 ms links with a 3-packet queue.
# All leaves ping each other in bursts, so the hub's queues overflow. The script reports the count of `d` (drop) events in `star.tr`.
# Vary `n`, the link bandwidth, the queue limit or the ping interval to see how drops change.
# `grep "^d" star.tr | awk '{print $5}' | sort | uniq -c` breaks drops down by packet type.
# Experiment 14: ping over a star topology with n nodes; count packets dropped due to congestion.
# Run: ns exp14_ns2_star_ping.tcl [n]     e.g. ns star_ping.tcl 6
# Node 0 is the hub; each leaf pings the next leaf, so all traffic passes through the hub.
# Slow links + small queues => congestion => drops.

set n 6
if {$argc > 0} { set n [lindex $argv 0] }

set ns [new Simulator]
set nf [open star.nam w]
$ns namtrace-all $nf
set tf [open star.tr w]
$ns trace-all $tf

Agent/Ping instproc recv {from rtt} {
    $self instvar node_
    puts "node [$node_ id] received ping answer from $from with round-trip-time $rtt ms."
}

proc finish {} {
    global ns nf tf
    $ns flush-trace
    close $nf
    close $tf
    set drops [exec sh -c "grep -c '^d' star.tr || true"]
    puts "\nTotal packets dropped due to congestion: $drops"
    exec nam star.nam &
    exit 0
}

for {set i 0} {$i < $n} {incr i} { set node($i) [$ns node] }

for {set i 1} {$i < $n} {incr i} {
    $ns duplex-link $node(0) $node($i) 0.1Mb 10ms DropTail
    $ns queue-limit $node(0) $node($i) 3
}

for {set i 1} {$i < $n} {incr i} {
    set p($i) [new Agent/Ping]
    $ns attach-agent $node($i) $p($i)
}

for {set i 1} {$i < $n} {incr i} {
    set j [expr {$i % ($n - 1) + 1}]
    $ns connect $p($i) $p($j)
}

for {set t 0.1} {$t < 2.0} {set t [expr {$t + 0.01}]} {
    for {set i 1} {$i < $n} {incr i} {
        $ns at $t "$p($i) send"
    }
}

$ns at 3.0 "finish"
$ns run
