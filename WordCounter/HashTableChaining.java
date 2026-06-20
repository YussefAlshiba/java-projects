/* HashTableChaining:
   Hash table with chaining (linked lists) for word frequency counting.
*/
public class HashTableChaining {

    private static class Node {
        String word;
        int count;
        Node next;
        Node(String word) {
            this.word = word;
            this.count = 1;
            this.next = null;
        }
    }

    private static final int TABLE_SIZE = 10007; // prime number
    private Node[] table = new Node[TABLE_SIZE];
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
        Node n = table[idx];
        while (n != null) {
            if (n.word.equals(word)) { n.count++; return; }
            n = n.next;
        }
        Node newNode = new Node(word);
        newNode.next = table[idx];
        table[idx] = newNode;
        size++;
    }

    public int size() { return size; }

    public String[] getMostFrequentWords() {
        int max = 0;
        for (Node head : table) {
            Node n = head;
            while (n != null) {
                if (n.count > max) max = n.count;
                n = n.next;
            }
        }
        java.util.List<String> result = new java.util.ArrayList<>();
        for (Node head : table) {
            Node n = head;
            while (n != null) {
                if (n.count == max) result.add(n.word + ": " + n.count);
                n = n.next;
            }
        }
        return result.toArray(new String[0]);
    }

    public String toStringOrderedAsc() {
        java.util.List<String> list = new java.util.ArrayList<>();
        for (Node head : table) {
            Node n = head;
            while (n != null) {
                list.add(n.word + ": " + n.count);
                n = n.next;
            }
        }
        java.util.Collections.sort(list);
        StringBuilder sb = new StringBuilder();
        for (String s : list) sb.append(s).append("\n");
        return sb.toString();
    }
}
