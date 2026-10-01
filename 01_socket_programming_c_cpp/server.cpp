// TCP server in C++: accepts one client at a time and echoes back every message.
// g++ server.cpp -o server_cpp && ./server_cpp [port]      (default 8081)
#include <iostream>
#include <string>
#include <cstring>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

int main(int argc, char *argv[]) {
    int port = argc > 1 ? std::stoi(argv[1]) : 8081;
    int srv = socket(AF_INET, SOCK_STREAM, 0);
    if (srv < 0) { perror("socket"); return 1; }
    int opt = 1;
    setsockopt(srv, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof opt);

    sockaddr_in addr{};
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = htonl(INADDR_ANY);
    addr.sin_port = htons(port);
    if (bind(srv, reinterpret_cast<sockaddr *>(&addr), sizeof addr) < 0) { perror("bind"); return 1; }
    if (listen(srv, 5) < 0) { perror("listen"); return 1; }
    std::cout << "C++ server listening on port " << port << "\n";

    for (;;) {
        sockaddr_in cli{};
        socklen_t len = sizeof cli;
        int c = accept(srv, reinterpret_cast<sockaddr *>(&cli), &len);
        if (c < 0) { perror("accept"); continue; }
        std::cout << "Client connected: " << inet_ntoa(cli.sin_addr) << ":" << ntohs(cli.sin_port) << "\n";
        char buf[1024];
        ssize_t n;
        while ((n = recv(c, buf, sizeof buf - 1, 0)) > 0) {
            std::string msg(buf, n);
            std::cout << "Received: " << msg;
            send(c, msg.data(), msg.size(), 0);
            if (msg.rfind("bye", 0) == 0) break;
        }
        std::cout << "Client disconnected\n";
        close(c);
    }
}
