/* Experiment 2: demonstrates common network system calls in one process. */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>

int main(void) {
    /* byte order */
    uint16_t port = 8080;
    printf("htons(%u)=%u  ntohs(htons)=%u\n", port, htons(port), ntohs(htons(port)));

    /* address conversion */
    struct in_addr a;
    inet_pton(AF_INET, "192.168.1.10", &a);
    char buf[INET_ADDRSTRLEN];
    printf("inet_pton -> 0x%08x ; inet_ntop -> %s\n", ntohl(a.s_addr),
           inet_ntop(AF_INET, &a, buf, sizeof buf));

    /* getaddrinfo (DNS) */
    struct addrinfo hints = {0}, *res;
    hints.ai_family = AF_INET; hints.ai_socktype = SOCK_STREAM;
    if (getaddrinfo("localhost", "80", &hints, &res) == 0) {
        struct sockaddr_in *s = (struct sockaddr_in *)res->ai_addr;
        printf("getaddrinfo(localhost) = %s\n", inet_ntop(AF_INET, &s->sin_addr, buf, sizeof buf));
        freeaddrinfo(res);
    }

    /* socket + setsockopt + bind + listen + getsockname */
    int fd = socket(AF_INET, SOCK_STREAM, 0);
    if (fd < 0) { perror("socket"); return 1; }
    int opt = 1;
    setsockopt(fd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof opt);
    struct sockaddr_in addr = {0};
    addr.sin_family = AF_INET; addr.sin_addr.s_addr = htonl(INADDR_LOOPBACK); addr.sin_port = 0; /* ephemeral */
    if (bind(fd, (struct sockaddr *)&addr, sizeof addr) < 0) { perror("bind"); return 1; }
    listen(fd, 5);
    socklen_t len = sizeof addr;
    getsockname(fd, (struct sockaddr *)&addr, &len);
    printf("server socket fd=%d listening on %s:%u\n", fd, inet_ntoa(addr.sin_addr), ntohs(addr.sin_port));

    /* client connects to it */
    int c = socket(AF_INET, SOCK_STREAM, 0);
    connect(c, (struct sockaddr *)&addr, sizeof addr);
    int s = accept(fd, NULL, NULL);
    send(c, "hello", 5, 0);
    char m[16] = {0};
    ssize_t n = recv(s, m, sizeof m - 1, 0);
    printf("recv() %zd bytes: %s\n", n, m);

    struct sockaddr_in peer; len = sizeof peer;
    getpeername(s, (struct sockaddr *)&peer, &len);
    printf("getpeername -> port %u\n", ntohs(peer.sin_port));

    int type; len = sizeof type;
    getsockopt(fd, SOL_SOCKET, SO_TYPE, &type, &len);
    printf("getsockopt SO_TYPE=%d (SOCK_STREAM=%d)\n", type, SOCK_STREAM);

    shutdown(c, SHUT_RDWR); close(c); close(s); close(fd);
    return 0;
}
