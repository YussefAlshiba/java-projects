/* WordCounter3:
   Word frequency counter using a Hash Table with Open Addressing (linear probing).
*/
import java.io.PrintStream;

public class WordCounter3 {

    public static void main(String[] args) {

        IO.print("input file name (incl. path) > ");
        String fileName = IO.readln();

        HashTableOpenAddressing ht = new HashTableOpenAddressing();
        WordReader wr = new WordReader(fileName);

        Timer.startTimer();
        String w = wr.readWord();
        while (w != null) {
            ht.add(w);
            w = wr.readWord();
        }
        Timer.stopTimer();

        IO.println("Number of distinct words: " + ht.size());
        IO.println("Time to build Hash Table (Open Addressing): " + Timer.elapsedTime());

        IO.println("\nMost frequent word(s):");
        for (String s : ht.getMostFrequentWords()) {
            IO.println("  " + s);
        }

        IO.print("\noutput file name (incl. path) > ");
        String outFileName = IO.readln();

        try {
            PrintStream ps = new PrintStream(outFileName);
            ps.print(ht.toStringOrderedAsc());
            ps.close();
            IO.println("Output written to " + outFileName);
        } catch (Exception e) {
            IO.println(e.toString());
        }
    }
}
