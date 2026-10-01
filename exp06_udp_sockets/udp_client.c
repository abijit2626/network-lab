/* Experiment 6: UDP client. gcc udp_client.c -o udp_client && ./udp_client [server_ip] [port] */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#define BUFSZ 1024

int main(int argc, char *argv[]) {
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
