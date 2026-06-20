public class Parser {

    private final Scanner scanner;

    private void expect(Symbol expectedSy) {
        if (scanner.sy != expectedSy) {
            throw new SyntaxError(expectedSy.toString().toLowerCase() + "expected");
        }
    }

    public Parser(Scanner scanner) {
        this.scanner = scanner;
    }

    public void parse() {
        IO.println("parsing started ...");
        MiniJava();
        IO.println("... parsing finished.");
    }

    public void MiniJava() {
        expect(Symbol.VOID);
        scanner.nextSy();
        expect(Symbol.MAIN);
        scanner.nextSy();
        expect(Symbol.OPEN_PAR);
        scanner.nextSy();
        expect(Symbol.CLOSE_PAR);
        scanner.nextSy();
        expect(Symbol.OPEN_BRACE);
        scanner.nextSy();
        if (scanner.sy == Symbol.INT) {
            VarDecl();
        }
        StatSeq();
        expect(Symbol.CLOSE_BRACE);
        scanner.nextSy();
        expect(Symbol.EOF);
    }

    private void VarDecl() {
        expect(Symbol.INT);
        scanner.nextSy();
        expect(Symbol.IDENT);
        scanner.nextSy();
        while (scanner.sy == Symbol.COMMA) {
            scanner.nextSy();
            expect(Symbol.IDENT);
            scanner.nextSy();
        }
        expect(Symbol.SEMICOLON);
        scanner.nextSy();
    }

    private void StatSeq() {
        Stat();
        while (scanner.sy == Symbol.IDENT ||
               scanner.sy == Symbol.PRINT ||
               scanner.sy == Symbol.SEMICOLON ||
               scanner.sy == Symbol.IF ||
               scanner.sy == Symbol.WHILE ||
               scanner.sy == Symbol.OPEN_BRACE) {
            Stat();
        }
    }

    private void Stat() {
        if (scanner.sy == Symbol.IDENT) {
            scanner.nextSy();
            expect(Symbol.ASSIGN);
            scanner.nextSy();
            Expr();
        } else if (scanner.sy == Symbol.PRINT) {
            scanner.nextSy();
            expect(Symbol.OPEN_PAR);
            scanner.nextSy();
            Expr();
            expect(Symbol.CLOSE_PAR);
            scanner.nextSy();
        } else if (scanner.sy == Symbol.IF) {
            scanner.nextSy();
            expect(Symbol.OPEN_PAR);
            scanner.nextSy();
            expect(Symbol.IDENT);
            scanner.nextSy();
            expect(Symbol.CLOSE_PAR);
            scanner.nextSy();
            Stat();
            if (scanner.sy == Symbol.ELSE) {
                scanner.nextSy();
                Stat();
            }
            return;
        } else if (scanner.sy == Symbol.WHILE) {
            scanner.nextSy();
            expect(Symbol.OPEN_PAR);
            scanner.nextSy();
            expect(Symbol.IDENT);
            scanner.nextSy();
            expect(Symbol.CLOSE_PAR);
            scanner.nextSy();
            Stat();
            return;
        } else if (scanner.sy == Symbol.OPEN_BRACE) {
            scanner.nextSy();
            StatSeq();
            expect(Symbol.CLOSE_BRACE);
            scanner.nextSy();
            return;
        }
        expect(Symbol.SEMICOLON);
        scanner.nextSy();
    }

    private void Expr() {
        Term();
        while (scanner.sy == Symbol.PLUS || scanner.sy == Symbol.MINUS) {
            scanner.nextSy();
            Term();
        }
    }

    private void Term() {
        Fact();
        while (scanner.sy == Symbol.TIMES || scanner.sy == Symbol.DIV) {
            scanner.nextSy();
            Fact();
        }
    }

    private void Fact() {
        switch (scanner.sy) {
            case Symbol.NUMBER:
                scanner.nextSy();
                break;
            case Symbol.IDENT:
                scanner.nextSy();
                break;
            case Symbol.READ:
                scanner.nextSy();
                expect(Symbol.OPEN_PAR);
                scanner.nextSy();
                expect(Symbol.CLOSE_PAR);
                scanner.nextSy();
                break;
            case Symbol.OPEN_PAR:
                scanner.nextSy();
                Expr();
                expect(Symbol.CLOSE_PAR);
                scanner.nextSy();
                break;
            default:
                throw new SyntaxError("number, ident, read or ( expected");
        }
    }
}
