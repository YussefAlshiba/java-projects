/* Edge:
   Represents an edge between two sites in the TouristGraph.
   The weight is calculated as distance (km) * beauty score.
   Beauty: 1 = wunderschoen, 2 = ganz nett, 3 = ok, 4 = Katastrophe
*/
public class Edge {

    private float distance;
    private int beauty;
    private float weight;

    public Edge(float distance, int beauty) {
        this.distance = distance;
        this.beauty = beauty;
        this.weight = distance * beauty;
    }

    public float getDistance() { return distance; }
    public int getBeauty() { return beauty; }
    public float getWeight() { return weight; }

    @Override
    public String toString() {
        return distance + " km, beauty " + beauty + " (weight " + weight + ")";
    }
}
