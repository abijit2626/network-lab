/* Experiment 6: UDP echo server. gcc udp_server.c -o udp_server && ./udp_server [port] */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#define BUFSZ 1024

int main(int argc, char *argv[]) {
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
