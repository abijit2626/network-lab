/* TCP client in C. gcc client.c -o client_c && ./client_c [server_ip] [port]   (type 'bye' to quit) */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

int main(int argc, char *argv[]) {
    const char *ip = argc > 1 ? argv[1] : "127.0.0.1";
    int port = argc > 2 ? atoi(argv[2]) : 8080;
    int s = socket(AF_INET, SOCK_STREAM, 0);
    if (s < 0) { perror("socket"); return 1; }

    struct sockaddr_in addr = {0};
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    if (inet_pton(AF_INET, ip, &addr.sin_addr) <= 0) { fprintf(stderr, "invalid address\n"); return 1; }
    if (connect(s, (struct sockaddr *)&addr, sizeof addr) < 0) { perror("connect"); return 1; }
    printf("Connected to %s:%d. Type messages ('bye' to quit)\n", ip, port);

    char buf[1024];
    while (printf("> "), fflush(stdout), fgets(buf, sizeof buf, stdin)) {
        send(s, buf, strlen(buf), 0);
        ssize_t n = recv(s, buf, sizeof buf - 1, 0);
        if (n <= 0) break;
        buf[n] = '\0';
        printf("Server echo: %s", buf);
        if (strncmp(buf, "bye", 3) == 0) break;
    }
    close(s);
    return 0;
}
