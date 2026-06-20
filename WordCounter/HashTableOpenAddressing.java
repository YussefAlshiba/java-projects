/* HashTableOpenAddressing:
   Hash table with open addressing (linear probing) for word frequency counting.
*/
public class HashTableOpenAddressing {

    private static final int TABLE_SIZE = 40009; // prime number, big enough
    private String[] keys = new String[TABLE_SIZE];
    private int[] counts = new int[TABLE_SIZE];
    private int size = 0;

    private int hash(String word) {
        int h = 0;
        for (int i = 0; i < word.length(); i++) {
            h = (h * 31 + word.charAt(i)) % TABLE_SIZE;
        }
        return Math.abs(h);
    }

    public void add(String word) {
        word = word.toLowerCase();
        int idx = hash(word);
        while (keys[idx] != null && !keys[idx].equals(word)) {
            idx = (idx + 1) % TABLE_SIZE; // linear probing
        }
        if (keys[idx] == null) {
            keys[idx] = word;
            counts[idx] = 1;
            size++;
        } else {
            counts[idx]++;
        }
    }

    public int size() { return size; }

    public String[] getMostFrequentWords() {
        int max = 0;
        for (int i = 0; i < TABLE_SIZE; i++) {
            if (keys[i] != null && counts[i] > max) max = counts[i];
        }
        java.util.List<String> result = new java.util.ArrayList<>();
        for (int i = 0; i < TABLE_SIZE; i++) {
            if (keys[i] != null && counts[i] == max)
                result.add(keys[i] + ": " + counts[i]);
        }
        return result.toArray(new String[0]);
    }

    public String toStringOrderedAsc() {
        java.util.List<String> list = new java.util.ArrayList<>();
        for (int i = 0; i < TABLE_SIZE; i++) {
            if (keys[i] != null) list.add(keys[i] + ": " + counts[i]);
        }
        java.util.Collections.sort(list);
        StringBuilder sb = new StringBuilder();
        for (String s : list) sb.append(s).append("\n");
        return sb.toString();
    }
}
