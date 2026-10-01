/* Experiment 5: TCP client-server (echo) in one file.
 * gcc exp05_tcp_sockets.c -o tcp && ./tcp server [port]   then   ./tcp client [ip] [port] */
#include <arpa/inet.h>
#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <unistd.h>

#define BUFSZ 1024

static int run_server(int argc, char *argv[]) {
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


static int run_client(int argc, char *argv[]) {
    const char *ip = argc > 1 ? argv[1] : "127.0.0.1";
    int port = argc > 2 ? atoi(argv[2]) : 8080;
    int s = socket(AF_INET, SOCK_STREAM, 0);
    if (s < 0) { perror("socket"); exit(1); }

    struct sockaddr_in addr = {0};
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    if (inet_pton(AF_INET, ip, &addr.sin_addr) <= 0) { fprintf(stderr, "bad address\n"); exit(1); }
    if (connect(s, (struct sockaddr *)&addr, sizeof addr) < 0) { perror("connect"); exit(1); }
    printf("Connected to %s:%d. Type messages ('bye' to quit)\n", ip, port);

    char buf[BUFSZ];
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

int main(int argc, char *argv[]) {
    if (argc < 2 || (strcmp(argv[1], "server") && strcmp(argv[1], "client"))) {
        fprintf(stderr, "usage: %s server [port] | client [server_ip] [port]\n", argv[0]);
        return 1;
    }
    /* shift arguments so that argv[1] is the first option of the chosen mode */
    return strcmp(argv[1], "server") == 0 ? run_server(argc - 1, argv + 1) : run_client(argc - 1, argv + 1);
}
