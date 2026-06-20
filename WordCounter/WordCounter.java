import java.io.PrintStream;

public class WordCounter {

    public static void main(String[] args) {
        
        IO.print("input file name (incl. path) > ");
        String fileName = IO.readln();

        WordReader wr = new WordReader(fileName);   
        IO.println("All words from " + fileName + ":");
        IO.println();

        int n = 0;
        String w = wr.readWord();      
        while (w != null) {
            n++;
            w = wr.readWord();
        }

        IO.println("File " + fileName + " contains " + n + " words.");

        IO.print("output file name (incl. path) > ");
        String outFileName = IO.readln();

        try {
            PrintStream ps = new PrintStream(outFileName);
            //print whatever you want to the file
            ps.close();            
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}