/* TouristGraph:
   Weighted directed graph for tourist routes between sites.
   Each edge stores an Edge object (distance, beauty, weight).
   Sites are identified by names, internally mapped to ids.
*/
import java.util.*;

public class TouristGraph {

    private int nrOfVertices;
    private Edge[][] adjacencyMatrix;
    private String[] siteNames; // id -> name
    private Map<String, Integer> nameToId; // name -> id

    public TouristGraph(int nrOfVertices) {
        this.nrOfVertices = nrOfVertices;
        this.adjacencyMatrix = new Edge[nrOfVertices][nrOfVertices];
        this.siteNames = new String[nrOfVertices];
        this.nameToId = new HashMap<>();
    }

    public void setSiteName(int id, String name) {
        siteNames[id] = name;
        nameToId.put(name, id);
    }

    public String getSiteName(int id) {
        return siteNames[id];
    }

    public int getIdOf(String name) {
        if (!nameToId.containsKey(name)) {
            throw new IllegalArgumentException("Unknown site: " + name);
        }
        return nameToId.get(name);
    }

    public int getNrOfVertices() {
        return nrOfVertices;
    }

    public void addEdge(int v, int w, float distance, int beauty) {
        adjacencyMatrix[v][w] = new Edge(distance, beauty);
    }

    public boolean hasEdge(int v, int w) {
        return adjacencyMatrix[v][w] != null;
    }

    public Edge getEdge(int v, int w) {
        return adjacencyMatrix[v][w];
    }

    public Iterable<Integer> getAdj(int v) {
        List<Integer> adjList = new ArrayList<>();
        for (int w = 0; w < nrOfVertices; w++) {
            if (adjacencyMatrix[v][w] != null) {
                adjList.add(w);
            }
        }
        return adjList;
    }
}
