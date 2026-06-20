/* Main:
   Reads a TouristGraph from a text file and tests bestRoute().
*/
import java.io.*;
import java.util.*;

public class Main {

    public static TouristGraph readGraph(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));

        int n = Integer.parseInt(br.readLine().trim());
        TouristGraph g = new TouristGraph(n);

        br.readLine(); // skip "sites"
        for (int i = 0; i < n; i++) {
            String[] parts = br.readLine().trim().split("\\s+", 2);
            int id = Integer.parseInt(parts[0]);
            String name = parts[1];
            g.setSiteName(id, name);
        }

        br.readLine(); // skip "stages"
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\s+");
            int v = Integer.parseInt(parts[0]);
            int w = Integer.parseInt(parts[1]);
            float distance = Float.parseFloat(parts[2]);
            int beauty = Integer.parseInt(parts[3]);
            g.addEdge(v, w, distance, beauty);
        }

        br.close();
        return g;
    }

    public static void main(String[] args) {
        try {
            TouristGraph g = readGraph("tourgraph.txt");

            BestRouteResult result = TouristGraphAlgorithms.bestRoute(g, "Hagenberg", "Kefermarkt");
            System.out.println("Best route from Hagenberg to Kefermarkt:");
            System.out.println(result);

            BestRouteResult result2 = TouristGraphAlgorithms.bestRoute(g, "Hagenberg", "Gutau");
            System.out.println("\nBest route from Hagenberg to Gutau:");
            System.out.println(result2);

        } catch (IOException e) {
            System.out.println("Error reading file: " + e);
        }
    }
}
