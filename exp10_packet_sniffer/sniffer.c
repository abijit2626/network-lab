/* Experiment 10: packet sniffer / protocol analyser using a raw AF_PACKET socket (Linux, needs root).
 * Decodes Ethernet -> IPv4 -> TCP/UDP/ICMP and tags application protocols by port.
 * gcc sniffer.c -o sniffer && sudo ./sniffer [count] [port] */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <net/ethernet.h>
#include <netinet/ip.h>
#include <netinet/tcp.h>
#include <netinet/udp.h>
#include <netinet/ip_icmp.h>

static const char *app(int p) {
    switch (p) {
    case 80: return "HTTP";   case 443: return "HTTPS"; case 21: return "FTP";
    case 25: case 587: case 2525: return "SMTP"; case 53: return "DNS";
    case 22: return "SSH";    case 23: return "TELNET";  case 67: case 68: return "DHCP";
    case 110: return "POP3";  case 143: return "IMAP";   default: return NULL;
    }
}

int main(int argc, char *argv[]) {
    int count = argc > 1 ? atoi(argv[1]) : 20;
    int filter = argc > 2 ? atoi(argv[2]) : 0;
    int s = socket(AF_PACKET, SOCK_RAW, htons(ETH_P_ALL));
    if (s < 0) { perror("socket (run as root)"); return 1; }

    unsigned char buf[65536];
    for (int n = 0; n < count;) {
        ssize_t len = recv(s, buf, sizeof buf, 0);
        if (len < (ssize_t)sizeof(struct ethhdr)) continue;
        struct ethhdr *eth = (struct ethhdr *)buf;
        if (ntohs(eth->h_proto) != ETH_P_IP || len < 14 + 20) continue;
        struct iphdr *ip = (struct iphdr *)(buf + 14);
        int ihl = ip->ihl * 4;
        unsigned char *t = buf + 14 + ihl;
        char src[16], dst[16];
        inet_ntop(AF_INET, &ip->saddr, src, sizeof src);
        inet_ntop(AF_INET, &ip->daddr, dst, sizeof dst);
        int sp = 0, dp = 0;
        char detail[160] = "";
        const unsigned char *payload = NULL; int plen = 0;

        if (ip->protocol == IPPROTO_TCP) {
            struct tcphdr *th = (struct tcphdr *)t;
            sp = ntohs(th->source); dp = ntohs(th->dest);
            snprintf(detail, sizeof detail, "TCP %d -> %d seq=%u ack=%u [%s%s%s%s%s%s]", sp, dp,
                     ntohl(th->seq), ntohl(th->ack_seq), th->syn ? "SYN " : "", th->ack ? "ACK " : "",
                     th->fin ? "FIN " : "", th->rst ? "RST " : "", th->psh ? "PSH " : "", th->urg ? "URG" : "");
            payload = t + th->doff * 4; plen = len - (payload - buf);
        } else if (ip->protocol == IPPROTO_UDP) {
            struct udphdr *uh = (struct udphdr *)t;
            sp = ntohs(uh->source); dp = ntohs(uh->dest);
            snprintf(detail, sizeof detail, "UDP %d -> %d len=%d", sp, dp, ntohs(uh->len));
            payload = t + 8; plen = len - (payload - buf);
        } else if (ip->protocol == IPPROTO_ICMP) {
            struct icmphdr *ih = (struct icmphdr *)t;
            snprintf(detail, sizeof detail, "ICMP type=%d code=%d (%s)", ih->type, ih->code,
                     ih->type == 8 ? "echo request" : ih->type == 0 ? "echo reply" : "other");
        } else snprintf(detail, sizeof detail, "protocol %d", ip->protocol);

        if (filter && sp != filter && dp != filter) continue;
        n++;
        printf("--- packet %d (%zd bytes)\n", n, len);
        printf("Ethernet %02x:%02x:%02x:%02x:%02x:%02x -> %02x:%02x:%02x:%02x:%02x:%02x\n",
               eth->h_source[0], eth->h_source[1], eth->h_source[2], eth->h_source[3], eth->h_source[4], eth->h_source[5],
               eth->h_dest[0], eth->h_dest[1], eth->h_dest[2], eth->h_dest[3], eth->h_dest[4], eth->h_dest[5]);
        printf("  IPv4 %s -> %s ttl=%d proto=%d total_len=%d\n", src, dst, ip->ttl, ip->protocol, ntohs(ip->tot_len));
        printf("  %s\n", detail);
        const char *a = app(dp) ? app(dp) : app(sp);
        if (a) {
            printf("  APP  %s\n", a);
            if (plen > 0 && payload && (!strcmp(a, "HTTP") || !strcmp(a, "FTP") || !strcmp(a, "SMTP"))) {
                printf("       ");
                for (int i = 0; i < plen && i < 80 && payload[i] != '\r' && payload[i] != '\n'; i++)
                    putchar(payload[i] >= 32 && payload[i] < 127 ? payload[i] : '.');
                putchar('\n');
            }
        }
    }
    close(s);
    return 0;
}
