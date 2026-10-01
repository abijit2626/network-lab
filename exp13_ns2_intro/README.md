# Experiment 13 – Introduction to NS-2
Install: `sudo apt install ns2 nam`. Run `ns first.tcl`. Key objects: `Simulator`, `node`, `duplex-link bw delay queue`,
`Agent/UDP|TCP` + `Agent/Null|TCPSink`, `Application/Traffic/CBR|FTP`, `$ns at time "cmd"`, trace file (`.tr`) and NAM file (`.nam`).
Trace line format: `event time from to type size flags fid src dst seq id` (`+` enqueue, `-` dequeue, `r` receive, `d` drop).
