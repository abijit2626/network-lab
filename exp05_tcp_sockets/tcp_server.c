/* Experiment 5: TCP echo server (iterative). gcc tcp_server.c -o tcp_server && ./tcp_server [port] */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#define BUFSZ 1024

int main(int argc, char *argv[]) {
    int port = argc > 1 ? atoi(argv[1]) : 8080;
    int srv = socket(AF_INET, SOCK_STREAM, 0);
    if (srv < 0) { perror("socket"); exit(1); }
    int opt = 1;
    setsockopt(srv, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof opt);

    struct sockaddr_in addr = {0};
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = INADDR_ANY;
    addr.sin_port = htons(port);
    if (bind(srv, (struct sockaddr *)&addr, sizeof addr) < 0) { perror("bind"); exit(1); }
    if (listen(srv, 5) < 0) { perror("listen"); exit(1); }
    printf("TCP server listening on port %d\n", port);

    for (;;) {
        struct sockaddr_in cli; socklen_t cl = sizeof cli;
        int c = accept(srv, (struct sockaddr *)&cli, &cl);
        if (c < 0) { perror("accept"); continue; }
        printf("Client connected: %s:%d\n", inet_ntoa(cli.sin_addr), ntohs(cli.sin_port));
        char buf[BUFSZ];
        ssize_t n;
        while ((n = recv(c, buf, sizeof buf - 1, 0)) > 0) {
            buf[n] = '\0';
            printf("Received: %s", buf);
            send(c, buf, n, 0);                 /* echo back */
            if (strncmp(buf, "bye", 3) == 0) break;
        }
        printf("Client disconnected\n");
        close(c);
    }
}
