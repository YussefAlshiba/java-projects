/* BST:
   Binary Search Tree for word frequency counting.
   Keys are words (case-insensitive), values are frequencies.
*/
public class BST {

    private static class Node {
        String word;
        int count;
        Node left, right;
        Node(String word) {
            this.word = word;
            this.count = 1;
        }
    }

    private Node root = null;
    private int size = 0;

    // insert or increment frequency
    public void add(String word) {
        root = addRec(root, word.toLowerCase());
    }

    private Node addRec(Node n, String word) {
        if (n == null) { size++; return new Node(word); }
        int cmp = word.compareTo(n.word);
        if (cmp < 0) n.left  = addRec(n.left,  word);
        else if (cmp > 0) n.right = addRec(n.right, word);
        else n.count++;
        return n;
    }

    public int size() { return size; }

    // returns all words with maximum frequency
    public String[] getMostFrequentWords() {
        int max = maxCount(root);
        java.util.List<String> result = new java.util.ArrayList<>();
        collectMax(root, max, result);
        return result.toArray(new String[0]);
    }

    private int maxCount(Node n) {
        if (n == null) return 0;
        return Math.max(n.count, Math.max(maxCount(n.left), maxCount(n.right)));
    }

    private void collectMax(Node n, int max, java.util.List<String> result) {
        if (n == null) return;
        collectMax(n.left, max, result);
        if (n.count == max) result.add(n.word + ": " + n.count);
        collectMax(n.right, max, result);
    }

    // returns alphabetically sorted list with frequencies
    public String toStringOrderedAsc() {
        StringBuilder sb = new StringBuilder();
        inorder(root, sb);
        return sb.toString();
    }

    private void inorder(Node n, StringBuilder sb) {
        if (n == null) return;
        inorder(n.left, sb);
        sb.append(n.word).append(": ").append(n.count).append("\n");
        inorder(n.right, sb);
    }
}
