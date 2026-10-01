/* Experiment 2: system calls used for network programming in Linux - SERVER side.
 *
 * socket() create endpoint · setsockopt() set options · bind() attach address · listen() mark passive ·
 * accept() take a connection · getsockname()/getpeername() local/remote address · recv()/send() TCP I/O ·
 * getsockopt() read options · shutdown()/close() · htons/htonl/ntohs/ntohl byte order · inet_ntop() address text.
 *
 * Build & run:  gcc exp02_server.c -o server && ./server [port]      (then run ./client in another terminal)
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

int main(int argc, char *argv[]) {
    int port = argc > 1 ? atoi(argv[1]) : 8000;

    /* 1. socket(): create a TCP endpoint */
    int srv = socket(AF_INET, SOCK_STREAM, 0);
    if (srv < 0) { perror("socket"); exit(1); }
    printf("socket()       -> fd %d\n", srv);

    /* 2. setsockopt(): allow quick restart on the same port */
    int opt = 1;
    setsockopt(srv, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof opt);
    printf("setsockopt()   -> SO_REUSEADDR enabled\n");

    /* 3. bind(): attach to a local address; htons/htonl convert to network byte order */
    struct sockaddr_in addr = {0};
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = htonl(INADDR_ANY);
    addr.sin_port = htons(port);
    if (bind(srv, (struct sockaddr *)&addr, sizeof addr) < 0) { perror("bind"); exit(1); }
    printf("bind()         -> port %d (htons=%u in network order)\n", port, htons(port));

    /* 4. listen(): make the socket passive with a backlog of 5 */
    if (listen(srv, 5) < 0) { perror("listen"); exit(1); }
    printf("listen()       -> backlog 5, waiting for a client...\n");

    /* 5. getsockname()/getsockopt(): inspect the listening socket */
    struct sockaddr_in local; socklen_t len = sizeof local;
    getsockname(srv, (struct sockaddr *)&local, &len);
    int type; socklen_t tl = sizeof type;
    getsockopt(srv, SOL_SOCKET, SO_TYPE, &type, &tl);
    printf("getsockname()  -> local port %d, getsockopt SO_TYPE=%d (SOCK_STREAM=%d)\n", ntohs(local.sin_port), type, SOCK_STREAM);

    /* 6. accept(): block until a client connects; returns a NEW socket for that client */
    struct sockaddr_in cli; len = sizeof cli;
    int c = accept(srv, (struct sockaddr *)&cli, &len);
    if (c < 0) { perror("accept"); exit(1); }
    char ip[INET_ADDRSTRLEN];
    inet_ntop(AF_INET, &cli.sin_addr, ip, sizeof ip);
    printf("accept()       -> client %s:%d on fd %d\n", ip, ntohs(cli.sin_port), c);

    struct sockaddr_in peer; len = sizeof peer;
    getpeername(c, (struct sockaddr *)&peer, &len);
    printf("getpeername()  -> peer port %d\n", ntohs(peer.sin_port));

    /* 7. recv()/send(): exchange data */
    char buf[1024];
    ssize_t n = recv(c, buf, sizeof buf - 1, 0);
    if (n > 0) {
        buf[n] = '\0';
        printf("recv()         -> %zd bytes: %s\n", n, buf);
        const char *reply = "Hello from server";
        send(c, reply, strlen(reply), 0);
        printf("send()         -> \"%s\"\n", reply);
    }

    /* 8. shutdown()/close(): terminate the connection */
    shutdown(c, SHUT_RDWR);
    close(c);
    close(srv);
    printf("shutdown()/close() -> done\n");
    return 0;
}
