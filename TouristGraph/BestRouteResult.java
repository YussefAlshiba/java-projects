/* BestRouteResult:
   Holds the result of the best route computation:
   total distance, total beauty score and the route (list of site names).
*/
import java.util.List;

public class BestRouteResult {

    private float totalDistance;
    private int totalBeauty;
    private List<String> route;

    public BestRouteResult(float totalDistance, int totalBeauty, List<String> route) {
        this.totalDistance = totalDistance;
        this.totalBeauty = totalBeauty;
        this.route = route;
    }

    public float getTotalDistance() { return totalDistance; }
    public int getTotalBeauty() { return totalBeauty; }
    public List<String> getRoute() { return route; }

    @Override
    public String toString() {
        return "Route: " + route +
               ", Distance: " + totalDistance + " km" +
               ", Beauty: " + totalBeauty;
    }
}
