/* WordReader:                                               HDO, 2025-03-15 */
/* ------                                                    DA,  2026-05-19 */
/* Simple but efficient class that provides objects to read single           */
/* words from  a text file (in UTF8 ecoding).                                */
/* Method readWord returns null on end of file.                              */
/* ATTENTION: save this source file with UTF8 encoding.                      */
/* ========================================================================= */

import java.io.*;

public class WordReader {
    private static int EOF = -1;

    private BufferedReader br = null;
    private StringBuilder  sb = null;
    private int ch; // current character as int value

    public WordReader(String fileName) {
        try {
            br = new BufferedReader(new FileReader(fileName));
            sb = new StringBuilder(100);
            ch = br.read();  // read first char
        } catch (IOException e) {
            System.err.println(e);
            System.exit(-1);
        } 
    }

    // isLetter is restricted to German letters
    private static boolean isLetter(int ch) {
        return (Character.isLetter(ch) ||
                (ch == '’') ||
                (ch == '-') );
    }       

    public String readWord() {
        try {
            while ( (ch != EOF) && !isLetter(ch) ) {
                ch = br.read();
            }
            if (ch == EOF) return null;
            sb.setLength(0);
            do {
                sb.append((char)ch);
                ch = br.read();
            } while (isLetter(ch));
            return sb.toString();
        } catch (IOException e) {
            System.err.println(e);
            System.exit(-1);
        }
        return null;
    } // readWord

}