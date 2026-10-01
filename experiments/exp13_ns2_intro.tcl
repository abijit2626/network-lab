# # Experiment 13 – Introduction to NS-2
# Install: `sudo apt install ns2 nam`. Run `ns exp13_ns2_intro.tcl`. Key objects: `Simulator`, `node`, `duplex-link bw delay queue`,
# `Agent/UDP|TCP` + `Agent/Null|TCPSink`, `Application/Traffic/CBR|FTP`, `$ns at time "cmd"`, trace file (`.tr`) and NAM file (`.nam`).
# Trace line format: `event time from to type size flags fid src dst seq id` (`+` enqueue, `-` dequeue, `r` receive, `d` drop).
# Experiment 13: Introduction to NS-2 - two nodes, one duplex link, UDP CBR traffic.
# Run: ns exp13_ns2_intro.tcl      (view with: nam first.nam)
set ns [new Simulator]

set nf [open first.nam w]
$ns namtrace-all $nf
set tf [open first.tr w]
$ns trace-all $tf

proc finish {} {
    global ns nf tf
    $ns flush-trace
    close $nf
    close $tf
    exec nam first.nam &
    exit 0
}

set n0 [$ns node]
set n1 [$ns node]
$ns duplex-link $n0 $n1 1Mb 10ms DropTail

set udp [new Agent/UDP]
$ns attach-agent $n0 $udp
set null [new Agent/Null]
$ns attach-agent $n1 $null
$ns connect $udp $null

set cbr [new Application/Traffic/CBR]
$cbr set packetSize_ 500
$cbr set interval_ 0.01
$cbr attach-agent $udp

$ns at 0.5 "$cbr start"
$ns at 4.5 "$cbr stop"
$ns at 5.0 "finish"
$ns run
