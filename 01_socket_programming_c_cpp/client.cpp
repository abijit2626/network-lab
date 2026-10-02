#include <iostream>
#include <string>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

int main(int argc, char *argv[]) {
    std::string ip = argc > 1 ? argv[1] : "127.0.0.1";
    int port = argc > 2 ? std::stoi(argv[2]) : 8081;
    int s = socket(AF_INET, SOCK_STREAM, 0);
    if (s < 0) { perror("socket"); return 1; }

    sockaddr_in addr{};
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    if (inet_pton(AF_INET, ip.c_str(), &addr.sin_addr) <= 0) { std::cerr << "invalid address\n"; return 1; }
    if (connect(s, reinterpret_cast<sockaddr *>(&addr), sizeof addr) < 0) { perror("connect"); return 1; }
    std::cout << "Connected to " << ip << ":" << port << ". Type messages ('bye' to quit)\n";

    std::string line;
    while (std::cout << "> " << std::flush, std::getline(std::cin, line)) {
        line += "\n";
        send(s, line.data(), line.size(), 0);
        char buf[1024];
        ssize_t n = recv(s, buf, sizeof buf - 1, 0);
        if (n <= 0) break;
        std::cout << "Server echo: " << std::string(buf, n);
        if (line.rfind("bye", 0) == 0) break;
    }
    close(s);
    return 0;
}
