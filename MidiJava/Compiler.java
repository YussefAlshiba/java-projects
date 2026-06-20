public class Compiler {

    private final Scanner scanner;
    private final SymbolTable symbolTable;
    private final CodeGenerator codeGenerator;

    private void semanticError(String msg) {
        System.err.println("semantic error: " + msg + " in " +
                           scanner.syLnr + ", column " + scanner.syCnr);
    }

    private void expect(Symbol expectedSy) {
        if (scanner.sy != expectedSy) {
            throw new SyntaxError(expectedSy.toString().toLowerCase() + "expected");
        }
    }

    public Compiler(Scanner scanner, CodeGenerator codeGenerator) {
        this.scanner = scanner;
        this.symbolTable = new SymbolTable();
        this.codeGenerator = codeGenerator;
    }

    public void compile() {
        IO.println("compiling started ...");
        MiniJava();
        IO.println("... compiling finished.");
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
        codeGenerator.emit(OpCode.END);
    }

    private void VarDecl() {
        expect(Symbol.INT);
        scanner.nextSy();
        expect(Symbol.IDENT);
        symbolTable.declVar(scanner.identStr);
        scanner.nextSy();
        while (scanner.sy == Symbol.COMMA) {
            scanner.nextSy();
            expect(Symbol.IDENT);
            if (!symbolTable.declVar(scanner.identStr)) {
                semanticError("multiple declaration");
            }
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
            boolean isDecl = symbolTable.isDecl(scanner.identStr);
            if (!isDecl) {
                semanticError("variable not declared");
            } else {
                codeGenerator.emit(OpCode.LOAD_ADDR,
                    symbolTable.addrOf(scanner.identStr));
            }
            scanner.nextSy();
            expect(Symbol.ASSIGN);
            scanner.nextSy();
            Expr();
            if (isDecl) codeGenerator.emit(OpCode.STORE);
        } else if (scanner.sy == Symbol.PRINT) {
            scanner.nextSy();
            expect(Symbol.OPEN_PAR);
            scanner.nextSy();
            Expr();
            codeGenerator.emit(OpCode.PRINT);
            expect(Symbol.CLOSE_PAR);
            scanner.nextSy();
        } else if (scanner.sy == Symbol.IF) {
            scanner.nextSy();
            expect(Symbol.OPEN_PAR);
            scanner.nextSy();
            String id = scanner.identStr;
            boolean isDecl = symbolTable.isDecl(id);
            if (!isDecl)
                semanticError("variable not declared");
            else
                codeGenerator.emit(OpCode.LOAD_VAL, symbolTable.addrOf(id));
            scanner.nextSy();
            expect(Symbol.CLOSE_PAR);
            scanner.nextSy();
            codeGenerator.emit(OpCode.JMPZ, 0);
            int addr = codeGenerator.curAddr() - 4;
            Stat();
            if (scanner.sy == Symbol.ELSE) {
                codeGenerator.emit(OpCode.JMP, 0);
                codeGenerator.fixUp(addr, codeGenerator.curAddr());
                addr = codeGenerator.curAddr() - 4;
                scanner.nextSy();
                Stat();
            }
            codeGenerator.fixUp(addr, codeGenerator.curAddr());
            return;
        } else if (scanner.sy == Symbol.WHILE) {
            scanner.nextSy();
            int addr1 = codeGenerator.curAddr();
            expect(Symbol.OPEN_PAR);
            scanner.nextSy();
            String id = scanner.identStr;
            boolean isDecl = symbolTable.isDecl(id);
            if (!isDecl)
                semanticError("variable not declared");
            else
                codeGenerator.emit(OpCode.LOAD_VAL, symbolTable.addrOf(id));
            scanner.nextSy();
            expect(Symbol.CLOSE_PAR);
            scanner.nextSy();
            codeGenerator.emit(OpCode.JMPZ, 0);
            int addr2 = codeGenerator.curAddr() - 4;
            Stat();
            codeGenerator.emit(OpCode.JMP, addr1);
            codeGenerator.fixUp(addr2, codeGenerator.curAddr());
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
            if (scanner.sy == Symbol.PLUS) {
                scanner.nextSy();
                Term();
                codeGenerator.emit(OpCode.ADD);
            } else {
                scanner.nextSy();
                Term();
                codeGenerator.emit(OpCode.SUB);
            }
        }
    }

    private void Term() {
        Fact();
        while (scanner.sy == Symbol.TIMES || scanner.sy == Symbol.DIV) {
            if (scanner.sy == Symbol.TIMES) {
                scanner.nextSy();
                Fact();
                codeGenerator.emit(OpCode.MUL);
            } else {
                scanner.nextSy();
                Fact();
                codeGenerator.emit(OpCode.DIV);
            }
        }
    }

    private void Fact() {
        switch (scanner.sy) {
            case Symbol.NUMBER:
                codeGenerator.emit(OpCode.LOAD_CONST, scanner.numberVal);
                scanner.nextSy();
                break;
            case Symbol.IDENT:
                if (!symbolTable.isDecl(scanner.identStr))
                    semanticError("variable not declared");
                else
                    codeGenerator.emit(OpCode.LOAD_VAL,
                        symbolTable.addrOf(scanner.identStr));
                scanner.nextSy();
                break;
            case Symbol.READ:
                scanner.nextSy();
                expect(Symbol.OPEN_PAR);
                codeGenerator.emit(OpCode.READ);
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
