/* Experiment 2: system calls used for network programming in Linux - CLIENT side.
 *
 * socket() · inet_pton() text->binary address · htons() · connect() active open · getsockname() (ephemeral port) ·
 * send()/recv() · close().
 *
 * Build & run:  gcc exp02_client.c -o client && ./client [server_ip] [port]   (start ./server first)
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

int main(int argc, char *argv[]) {
    const char *ip = argc > 1 ? argv[1] : "127.0.0.1";
    int port = argc > 2 ? atoi(argv[2]) : 8000;

    /* 1. socket() */
    int s = socket(AF_INET, SOCK_STREAM, 0);
    if (s < 0) { perror("socket"); exit(1); }
    printf("socket()       -> fd %d\n", s);

    /* 2. inet_pton() + htons(): build the server address */
    struct sockaddr_in addr = {0};
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    if (inet_pton(AF_INET, ip, &addr.sin_addr) <= 0) { fprintf(stderr, "invalid address %s\n", ip); exit(1); }
    printf("inet_pton()    -> %s = 0x%08x\n", ip, ntohl(addr.sin_addr.s_addr));

    /* 3. connect(): TCP three-way handshake */
    if (connect(s, (struct sockaddr *)&addr, sizeof addr) < 0) { perror("connect"); exit(1); }
    printf("connect()      -> connected to %s:%d\n", ip, port);

    /* 4. getsockname(): the kernel picked an ephemeral local port for us */
    struct sockaddr_in local; socklen_t len = sizeof local;
    getsockname(s, (struct sockaddr *)&local, &len);
    printf("getsockname()  -> local ephemeral port %d\n", ntohs(local.sin_port));

    /* 5. send()/recv() */
    const char *msg = "Hello from client";
    send(s, msg, strlen(msg), 0);
    printf("send()         -> \"%s\"\n", msg);
    char buf[1024];
    ssize_t n = recv(s, buf, sizeof buf - 1, 0);
    if (n > 0) { buf[n] = '\0'; printf("recv()         -> %zd bytes: %s\n", n, buf); }

    /* 6. close() */
    close(s);
    printf("close()        -> done\n");
    return 0;
}
