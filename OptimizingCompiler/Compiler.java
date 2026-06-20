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
            AST tree = Expr();
            tree = optimize(tree);
            if (isDecl) {
                codeGenerator.emitCodeForAST(tree);
                codeGenerator.emit(OpCode.STORE);
            }
        } else if (scanner.sy == Symbol.PRINT) {
            scanner.nextSy();
            expect(Symbol.OPEN_PAR);
            scanner.nextSy();
            AST tree = Expr();
            tree = optimize(tree);
            codeGenerator.emitCodeForAST(tree);
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

    private AST Expr() {
    AST left = Term();
    while (scanner.sy == Symbol.PLUS ||
           scanner.sy == Symbol.MINUS) {
      String op = (scanner.sy == Symbol.PLUS) ? "+" : "-";
      scanner.nextSy();
      AST right = Term();
      left = new AST(op, left, right);
    }
    return left;
  }

  private AST Term() {
    AST left = Fact();
    while (scanner.sy == Symbol.TIMES ||
           scanner.sy == Symbol.DIV) {
      String op = (scanner.sy == Symbol.TIMES) ? "*" : "/";
      scanner.nextSy();
      AST right = Fact();
      left = new AST(op, left, right);
    }
    return left;
  }

  private AST Fact() {
    switch (scanner.sy) {
      case Symbol.NUMBER:
        AST num = new AST(String.valueOf(scanner.numberVal));
        scanner.nextSy();
        return num;
      case Symbol.IDENT:
        if (!symbolTable.isDecl(scanner.identStr))
          semanticError("variable not declared");
        AST id = new AST(scanner.identStr + "@" + 
                   (symbolTable.isDecl(scanner.identStr) ? 
                    symbolTable.addrOf(scanner.identStr) : 0));
        scanner.nextSy();
        return id;
      case Symbol.READ:
        scanner.nextSy();
        expect(Symbol.OPEN_PAR);
        codeGenerator.emit(OpCode.READ);
        scanner.nextSy();
        expect(Symbol.CLOSE_PAR);
        scanner.nextSy();
        return new AST("__read__");
      case Symbol.OPEN_PAR:
        scanner.nextSy();
        AST inner = Expr();
        expect(Symbol.CLOSE_PAR);
        scanner.nextSy();
        return inner;
      default:
        throw new SyntaxError("number, ident, read or ( expected");
    }
  }
/* optimize:
       Transforms AST by eliminating redundant operations
       and folding constant subexpressions.
    */
    private AST optimize(AST t) {
        if (t == null || t.left == null) return t;
        // recurse first
        t.left = optimize(t.left);
        t.right = optimize(t.right);
        // constant folding
        try {
            int l = Integer.parseInt(t.left.val);
            int r = Integer.parseInt(t.right.val);
            switch (t.val) {
                case "+": return new AST(String.valueOf(l + r));
                case "-": return new AST(String.valueOf(l - r));
                case "*": return new AST(String.valueOf(l * r));
                case "/": if (r != 0) return new AST(String.valueOf(l / r));
            }
        } catch (NumberFormatException e) {}
        // eliminate redundant operations
        try { if (Integer.parseInt(t.left.val) == 0 && t.val.equals("+")) return t.right; } catch (NumberFormatException e) {}
        try { if (Integer.parseInt(t.right.val) == 0 && t.val.equals("+")) return t.left; } catch (NumberFormatException e) {}
        try { if (Integer.parseInt(t.right.val) == 0 && t.val.equals("-")) return t.left; } catch (NumberFormatException e) {}
        try { if (Integer.parseInt(t.left.val) == 1 && t.val.equals("*")) return t.right; } catch (NumberFormatException e) {}
        try { if (Integer.parseInt(t.right.val) == 1 && t.val.equals("*")) return t.left; } catch (NumberFormatException e) {}
        try { if (Integer.parseInt(t.right.val) == 1 && t.val.equals("/")) return t.left; } catch (NumberFormatException e) {}
        return t;
    }
}
