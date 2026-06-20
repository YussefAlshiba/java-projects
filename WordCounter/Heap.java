/* Heap:
   Min-Heap of fixed size k to find the k most frequent names.
   Each entry stores a name and its frequency.
*/
public class Heap {

    private String[] names;
    private int[] counts;
    private int size;
    private final int maxSize;

    public Heap(int k) {
        this.maxSize = k;
        this.names = new String[k];
        this.counts = new int[k];
        this.size = 0;
    }

    private void swap(int i, int j) {
        String tmpN = names[i]; names[i] = names[j]; names[j] = tmpN;
        int tmpC = counts[i]; counts[i] = counts[j]; counts[j] = tmpC;
    }

    private void siftUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (counts[i] < counts[parent]) {
                swap(i, parent);
                i = parent;
            } else break;
        }
    }

    private void siftDown(int i) {
        while (true) {
            int left = 2 * i + 1, right = 2 * i + 2, smallest = i;
            if (left < size && counts[left] < counts[smallest]) smallest = left;
            if (right < size && counts[right] < counts[smallest]) smallest = right;
            if (smallest != i) { swap(i, smallest); i = smallest; }
            else break;
        }
    }

    public void insert(String name, int count) {
        if (size < maxSize) {
            names[size] = name;
            counts[size] = count;
            size++;
            siftUp(size - 1);
        } else if (count > counts[0]) {
            names[0] = name;
            counts[0] = count;
            siftDown(0);
        }
    }

    public String[] getNames() {
        String[] result = new String[size];
        for (int i = 0; i < size; i++) result[i] = names[i];
        return result;
    }
}
