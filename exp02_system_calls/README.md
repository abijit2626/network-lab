# Experiment 2 – System calls for network programming
`socket()` create endpoint · `bind()` attach address · `listen()` mark passive · `accept()` take a connection ·
`connect()` active open · `send()/recv()` TCP I/O · `sendto()/recvfrom()` UDP I/O · `close()/shutdown()` ·
`getsockname()/getpeername()` · `setsockopt()/getsockopt()` · `gethostbyname()/getaddrinfo()` · `htons/htonl/ntohs/ntohl` · `inet_pton/inet_ntop` · `select()/poll()`.

Build & run: `gcc syscalls_demo.c -o syscalls_demo && ./syscalls_demo`
