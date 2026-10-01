/* Experiment 6: UDP client-server (echo) in one file.
 * gcc exp06_udp_sockets.c -o udp && ./udp server [port]   then   ./udp client [ip] [port] */
#include <arpa/inet.h>
#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <unistd.h>

#define BUFSZ 1024

static int run_server(int argc, char *argv[]) {
    int port = argc > 1 ? atoi(argv[1]) : 9090;
    int s = socket(AF_INET, SOCK_DGRAM, 0);
    if (s < 0) { perror("socket"); exit(1); }
    struct sockaddr_in addr = {0}, cli;
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = INADDR_ANY;
    addr.sin_port = htons(port);
    if (bind(s, (struct sockaddr *)&addr, sizeof addr) < 0) { perror("bind"); exit(1); }
    printf("UDP server listening on port %d\n", port);

    char buf[BUFSZ];
    for (;;) {
        socklen_t cl = sizeof cli;
        ssize_t n = recvfrom(s, buf, sizeof buf - 1, 0, (struct sockaddr *)&cli, &cl);
        if (n < 0) { perror("recvfrom"); continue; }
        buf[n] = '\0';
        printf("From %s:%d -> %s", inet_ntoa(cli.sin_addr), ntohs(cli.sin_port), buf);
        sendto(s, buf, n, 0, (struct sockaddr *)&cli, cl);
    }
}


static int run_client(int argc, char *argv[]) {
    const char *ip = argc > 1 ? argv[1] : "127.0.0.1";
    int port = argc > 2 ? atoi(argv[2]) : 9090;
    int s = socket(AF_INET, SOCK_DGRAM, 0);
    if (s < 0) { perror("socket"); exit(1); }
    struct timeval tv = {3, 0};                       /* UDP is unreliable: don't wait forever */
    setsockopt(s, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof tv);

    struct sockaddr_in addr = {0};
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    inet_pton(AF_INET, ip, &addr.sin_addr);

    char buf[BUFSZ];
    while (printf("> "), fflush(stdout), fgets(buf, sizeof buf, stdin)) {
        sendto(s, buf, strlen(buf), 0, (struct sockaddr *)&addr, sizeof addr);
        ssize_t n = recvfrom(s, buf, sizeof buf - 1, 0, NULL, NULL);
        if (n < 0) { puts("timeout - no reply (datagram lost?)"); continue; }
        buf[n] = '\0';
        printf("Server echo: %s", buf);
    }
    close(s);
    return 0;
}

int main(int argc, char *argv[]) {
    if (argc < 2 || (strcmp(argv[1], "server") && strcmp(argv[1], "client"))) {
        fprintf(stderr, "usage: %s server [port] | client [server_ip] [port]\n", argv[0]);
        return 1;
    }
    /* shift arguments so that argv[1] is the first option of the chosen mode */
    return strcmp(argv[1], "server") == 0 ? run_server(argc - 1, argv + 1) : run_client(argc - 1, argv + 1);
}
