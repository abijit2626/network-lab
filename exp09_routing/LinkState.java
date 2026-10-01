import java.util.*;

/** Experiment 9b: Link State routing - each router knows the full topology and runs Dijkstra.
 *  javac LinkState.java && java LinkState [--input] */
public class LinkState {
    static final int INF = 999;

    public static void main(String[] args) {
        int[][] cost = {{0, 2, INF, 1}, {2, 0, 3, 7}, {INF, 3, 0, 11}, {1, 7, 11, 0}};
        if (args.length > 0 && args[0].equals("--input")) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Number of routers: ");
            int m = sc.nextInt();
            cost = new int[m][m];
            System.out.println("Enter cost matrix (999 = no link):");
            for (int i = 0; i < m; i++) for (int j = 0; j < m; j++) cost[i][j] = sc.nextInt();
        }
        int n = cost.length;
        for (int src = 0; src < n; src++) {
            int[] dist = new int[n], prev = new int[n];
            boolean[] done = new boolean[n];
            Arrays.fill(dist, INF);
            Arrays.fill(prev, -1);
            dist[src] = 0;
            for (int c = 0; c < n; c++) {
                int u = -1;
                for (int i = 0; i < n; i++) if (!done[i] && (u < 0 || dist[i] < dist[u])) u = i;
                if (dist[u] == INF) break;
                done[u] = true;
                for (int v = 0; v < n; v++)
                    if (!done[v] && cost[u][v] != INF && dist[u] + cost[u][v] < dist[v]) { dist[v] = dist[u] + cost[u][v]; prev[v] = u; }
            }
            System.out.println("\nShortest paths from router " + src);
            for (int d = 0; d < n; d++) {
                if (d == src) continue;
                Deque<Integer> path = new ArrayDeque<>();
                for (int x = d; x != -1; x = prev[x]) path.push(x);
                StringBuilder sb = new StringBuilder();
                for (int x : path) sb.append(sb.length() > 0 ? " -> " : "").append(x);
                System.out.printf("  to %d: cost %3d  path %s%n", d, dist[d], dist[d] == INF ? "unreachable" : sb);
            }
        }
    }
}
