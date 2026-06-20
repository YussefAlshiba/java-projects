/* MaxNames:
   Finds the k most frequent names in an unsorted array using
   a HashMap for counting and a Min-Heap of size k for selection.
*/
import java.io.*;
import java.util.*;

public class MaxNames {

    public static String[] maxNames(String[] names, int k) {
        // count frequencies with HashMap
        HashMap<String, Integer> freq = new HashMap<>();
        for (String name : names) {
            freq.put(name, freq.getOrDefault(name, 0) + 1);
        }

        // use min-heap of size k to find k most frequent
        Heap heap = new Heap(k);
        for (Map.Entry<String, Integer> entry : freq.entrySet()) {
            heap.insert(entry.getKey(), entry.getValue());
        }

        return heap.getNames();
    }

    public static void main(String[] args) {
        // small test
        String[] test = {"Noah", "Sophie", "Lea", "Noah", "Sophie",
                         "Elias", "Noah", "Elias", "Lena", "Elias", "Elias"};
        IO.println("Test with small array, k=3:");
        String[] result = maxNames(test, 3);
        for (String s : result) IO.println("  " + s);

        // test with names.txt
        IO.println("\nTest with names.txt, k=5:");
        try {
            BufferedReader br = new BufferedReader(new FileReader("names.txt"));
            java.util.List<String> list = new java.util.ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) list.add(line);
            }
            br.close();
            String[] nameArr = list.toArray(new String[0]);
            IO.println("Total names read: " + nameArr.length);

            Timer.startTimer();
            String[] top = maxNames(nameArr, 5);
            Timer.stopTimer();

            IO.println("Top 5 names:");
            for (String s : top) IO.println("  " + s);
            IO.println("Time: " + Timer.elapsedTime());

        } catch (Exception e) {
            IO.println("Error: " + e.toString());
        }
    }
}
