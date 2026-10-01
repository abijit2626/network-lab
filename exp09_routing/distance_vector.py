#!/usr/bin/env python3
"""Experiment 9a: Distance Vector Routing (Bellman-Ford). Reads a cost matrix or uses a sample (999 = no link)."""
INF = 999
SAMPLE = [[0, 2, INF, 1],
          [2, 0, 3, 7],
          [INF, 3, 0, 11],
          [1, 7, 11, 0]]


def distance_vector(cost):
    n = len(cost)
    dist = [row[:] for row in cost]
    nxt = [[j if cost[i][j] != INF else -1 for j in range(n)] for i in range(n)]
    it = 0
    changed = True
    while changed:
        changed = False
        it += 1
        for i in range(n):
            for j in range(n):
                for k in range(n):               # i learns k's vector (k must be a neighbour)
                    if cost[i][k] != INF and dist[i][k] + dist[k][j] < dist[i][j]:
                        dist[i][j] = dist[i][k] + dist[k][j]
                        nxt[i][j] = nxt[i][k]
                        changed = True
        print(f"\nAfter iteration {it}:")
        for i in range(n):
            print(f"  Router {i}: " + " ".join(f"{d:>3}" for d in dist[i]))
    return dist, nxt


if __name__ == '__main__':
    import sys
    if '--input' in sys.argv:
        n = int(input("Number of routers: "))
        print("Enter cost matrix (999 = no link):")
        cost = [list(map(int, input().split())) for _ in range(n)]
    else:
        cost = SAMPLE
    dist, nxt = distance_vector(cost)
    for i in range(len(cost)):
        print(f"\nRouting table of router {i}")
        print("Dest  Next-hop  Cost")
        for j in range(len(cost)):
            print(f"{j:>4}  {('-' if i == j else nxt[i][j]):>8}  {dist[i][j]:>4}")
