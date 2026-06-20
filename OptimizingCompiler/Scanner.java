import java.io.BufferedReader;

public class Scanner {
    
    private static final char EOF = 0;
    public static final char ERROR = 1;

    private final BufferedReader reader;

    private String line;
    private char ch;
    private int chCnr, chLnr;

    public Symbol sy;
    public int syCnr, syLnr;

    public int numberVal;
    public String identStr;

    public Scanner(BufferedReader reader) {
        this.reader = reader;
        this.line = "";
        chLnr = 0;
        chCnr = 1;
        nextCh();
        nextSy();
    }

    private void nextCh() {
        try {
            if (line == null) {
                ch = EOF;
            } else {
                if (chCnr >= line.length()) {
                    line = reader.readLine();
                    chLnr++;
                    chCnr = 0;
                    ch = ' ';
                } else {
                    ch = line.charAt(chCnr);
                    chCnr++;
                }
            }
        } catch (Exception e) {
            ch = ERROR;
        }
    }

    public void nextSy() {
        while ((ch == ' ') || (ch == '\t')) {
            nextCh();
        }
        syCnr = chCnr;
        syLnr = chLnr;
        switch (ch) {
            case EOF:   sy = Symbol.EOF;                   break;
            case ERROR: sy = Symbol.ERROR;                 break;
            case '+':   sy = Symbol.PLUS;        nextCh(); break;
            case '-':   sy = Symbol.MINUS;       nextCh(); break;
            case '*':   sy = Symbol.TIMES;       nextCh(); break;
            case '/':   sy = Symbol.DIV;         nextCh(); break;
            case '(':   sy = Symbol.OPEN_PAR;    nextCh(); break;
            case ')':   sy = Symbol.CLOSE_PAR;   nextCh(); break;
            case '{':   sy = Symbol.OPEN_BRACE;  nextCh(); break;
            case '}':   sy = Symbol.CLOSE_BRACE; nextCh(); break;
            case ',':   sy = Symbol.COMMA;       nextCh(); break;
            case ';':   sy = Symbol.SEMICOLON;   nextCh(); break;
            case '=':   sy = Symbol.ASSIGN;      nextCh(); break;
            default:
                if (Character.isLetter(ch)) {
                    StringBuilder sb = new StringBuilder();
                    do {
                        sb.append(ch);
                        nextCh();
                    } while (Character.isLetterOrDigit(ch));
                    identStr = sb.toString();
                    switch (identStr) {
                        case "int":   sy = Symbol.INT;   break;
                        case "main":  sy = Symbol.MAIN;  break;
                        case "print": sy = Symbol.PRINT; break;
                        case "read":  sy = Symbol.READ;  break;
                        case "void":  sy = Symbol.VOID;  break;
                        case "if":    sy = Symbol.IF;    break;
                        case "else":  sy = Symbol.ELSE;  break;
                        case "while": sy = Symbol.WHILE; break;
                        default:      sy = Symbol.IDENT;
                    }
                } else if (Character.isDigit(ch)) {
                    sy = Symbol.NUMBER;
                    numberVal = 0;
                    do {
                        numberVal = numberVal * 10 + (ch - '0');
                        nextCh();
                    } while (Character.isDigit(ch));
                } else {
                    sy = Symbol.ERROR;
                }
        }
    }
}
