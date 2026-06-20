/* WordCounter1:
   Word frequency counter using a Binary Search Tree (BST).
*/
import java.io.PrintStream;

public class WordCounter1 {

    public static void main(String[] args) {

        IO.print("input file name (incl. path) > ");
        String fileName = IO.readln();

        BST bst = new BST();
        WordReader wr = new WordReader(fileName);

        Timer.startTimer();
        String w = wr.readWord();
        while (w != null) {
            bst.add(w);
            w = wr.readWord();
        }
        Timer.stopTimer();

        IO.println("Number of distinct words: " + bst.size());
        IO.println("Time to build BST: " + Timer.elapsedTime());

        IO.println("\nMost frequent word(s):");
        for (String s : bst.getMostFrequentWords()) {
            IO.println("  " + s);
        }

        IO.print("\noutput file name (incl. path) > ");
        String outFileName = IO.readln();

        try {
            PrintStream ps = new PrintStream(outFileName);
            ps.print(bst.toStringOrderedAsc());
            ps.close();
            IO.println("Output written to " + outFileName);
        } catch (Exception e) {
            IO.println(e.toString());
        }
    }
}
