/* TouristGraphAlgorithms:
   Dijkstra-based algorithm to find the best route (lowest weight)
   between two sites in a TouristGraph.
*/
import java.util.*;

public class TouristGraphAlgorithms {

    public static BestRouteResult bestRoute(TouristGraph g, String start, String destination) {
        int startId = g.getIdOf(start);
        int destId = g.getIdOf(destination);
        int n = g.getNrOfVertices();

        final float INFINITY = Float.MAX_VALUE;
        boolean[] marked = new boolean[n];
        float[] weight = new float[n];
        float[] distance = new float[n];
        int[] beauty = new int[n];
        int[] predecessor = new int[n];

        for (int i = 0; i < n; i++) {
            weight[i] = INFINITY;
            predecessor[i] = -1;
        }
        weight[startId] = 0;
        distance[startId] = 0;
        beauty[startId] = 0;

        for (int count = 0; count < n; count++) {
            // find unmarked vertex with smallest weight
            int selected = -1;
            float minWeight = INFINITY;
            for (int v = 0; v < n; v++) {
                if (!marked[v] && weight[v] < minWeight) {
                    minWeight = weight[v];
                    selected = v;
                }
            }
            if (selected == -1) break; // no more reachable vertices
            marked[selected] = true;

            for (int v : g.getAdj(selected)) {
                if (!marked[v]) {
                    Edge e = g.getEdge(selected, v);
                    float altWeight = weight[selected] + e.getWeight();
                    if (altWeight < weight[v]) {
                        weight[v] = altWeight;
                        distance[v] = distance[selected] + e.getDistance();
                        beauty[v] = beauty[selected] + e.getBeauty();
                        predecessor[v] = selected;
                    }
                }
            }
        }

        // reconstruct route
        List<String> route = new ArrayList<>();
        if (weight[destId] == INFINITY) {
            return new BestRouteResult(0, 0, route); // no route found
        }
        int cur = destId;
        while (cur != -1) {
            route.add(0, g.getSiteName(cur));
            cur = predecessor[cur];
        }

        return new BestRouteResult(distance[destId], beauty[destId], route);
    }
}
