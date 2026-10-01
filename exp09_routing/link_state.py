#!/usr/bin/env python3
"""Experiment 9b: Link State Routing - every router knows the whole topology and runs Dijkstra."""
import heapq
INF = 999
SAMPLE = [[0, 2, INF, 1],
          [2, 0, 3, 7],
          [INF, 3, 0, 11],
          [1, 7, 11, 0]]


def dijkstra(cost, src):
    n = len(cost)
    dist = [INF] * n
    prev = [-1] * n
    dist[src] = 0
    pq = [(0, src)]
    while pq:
        d, u = heapq.heappop(pq)
        if d > dist[u]:
            continue
        for v in range(n):
            if v != u and cost[u][v] != INF and d + cost[u][v] < dist[v]:
                dist[v] = d + cost[u][v]
                prev[v] = u
                heapq.heappush(pq, (dist[v], v))
    return dist, prev


def path(prev, dst):
    p = []
    while dst != -1:
        p.append(dst)
        dst = prev[dst]
    return p[::-1]


if __name__ == '__main__':
    import sys
    if '--input' in sys.argv:
        n = int(input("Number of routers: "))
        print("Enter cost matrix (999 = no link):")
        cost = [list(map(int, input().split())) for _ in range(n)]
    else:
        cost = SAMPLE
    for s in range(len(cost)):
        dist, prev = dijkstra(cost, s)
        print(f"\nShortest paths from router {s}")
        for d in range(len(cost)):
            if d != s:
                print(f"  to {d}: cost {dist[d]:>3}  path {' -> '.join(map(str, path(prev, d)))}")
