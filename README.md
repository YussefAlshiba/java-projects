
==============================================================================

## 1. MidiJava
Wesentliche Sprachkonstrukte, die MiniJava fehlen, sind Verzweigungen und Schleifen. Also erwei-tern wir MiniJava um die binäre Verzweigung (`if`-Anweisung), die Abweisschleife (`while`-Schleife) sowie die Blockanweisung (…  `{` ... `}` …) – und taufen die neue Sprache MidiJava.
Nachdem wir mit dem Datentyp `int` und ohne Erweiterungen der Ausdrücke um relationale Operatoren auskommen wollen, verwenden wir für Bedingungen in Verzweigungen und Schleifen `int`-Variablen mit der Semantik, dass jeder Wert ungleich 0 als `true` und (nur) der Wert 0 als `false` interpretiert wird. Folgende Tabelle zeigt zur Verdeutlichung eine Abbildung von MidiJava auf (vollständiges) Java:


| MidiJava        | (vollständiges) Java |
| -------------   | -------------------- |
| `int x;`        | `int x;`             |
|  `if (x) ...`   | `if (x != 0) ...`    |
| `while (x) ...` | `while (x != 0) ...` |

Mit diesen Spracherweiterungen könnte man dann z. B. ein MidiJava-Programm schreiben, das für eine eingegebene Zahl `n` die Fakultät `f = n!` iterativ berechnet und diese ausgibt. Siehe folgendes Quelltextstück.
```java
f = n; n = n - 1;
while (n) {
  f = n * f;
  n = n - 1;
}
print(f);
```

Damit diese neuen Sprachkonstrukte im Compiler umgesetzt werden können, sind zwei neue Bytecode-Befehle notwendig. Folgende Tabelle erläutert diese beiden Befehle:
| Bytecode-Befehl      | Semantik |
| ------------- | -------------------- |
| `JMP   addr`  | Springe an die Codeadresse `addr` |
| `JMPZ  addr`  | Hole oberstes Element vom Stapel und wenn dieses `0` (`zero`) ist, springe nach `addr` |

Nun muss man nur noch klären, welche Bytecodestücke für die einzelnen, neuen MidiJava-Anweisungen zu erzeugen sind. Folgende Tabelle stellt die notwendigen Transformationen anhand von Mustern dar:

| MidiJava        | Bytecode (mit fiktiven Adressen) |
| -------------   | -------------------- |
| ` if (x)` <br> <br> &nbsp; &nbsp; `   then stat(s) `  <br>  `...`| `...    LOAD_VAL x`   <br> `...` **`    JMPZ 199`**  <br>  *`...    code for then stat(s)`* <br> **`199`** `...` |
|  `if (x)` <br> <br> &nbsp; &nbsp; *`  then stat(s)`* <br> <br> `else` <br> &nbsp; &nbsp; *`  else stat(s)`* <br> <br> `...` | `...    LOAD_VAL x` <br> `...` **`    JMPZ 166`** <br> *`...    code for then stat(s)`* <br> `...` **`JMP 199`** <br> <br> **`166`** *`    code for else stat(s)`* <br> **`199`** `...` <br><br>
|  `while (x)` <br><br>  &nbsp; &nbsp; *`  while stat(s)`* <br><br><br>     |  **`166`** `LOAD_VAL x` <br> `171`   **`    JMPZ 199`** <br> *`...    code for while stat(s)`* <br> `...` **`JMP 166`** <br> **`199`** `...` |

Bei der Implementierung dieser neuen Sprachkonstrukte tritt das Problem auf, für die Bedingungen auch Sprunganweisungen „nach unten“ erzeugen zu müssen, wobei die Zieladressen der Sprünge noch nicht bekannt sind. Dieses Problem kann mit dem so genannten *Anderthalbpass-Verfahren* gelöst werden: Man erzeugt zuerst eine Sprunganweisung mit einer fiktiven Adresse (z. B. 0) und korri-giert diese später, sobald die Zieladresse bekannt ist (mittels `fixUp`).

Im Moodle-Kurs finden Sie in `ForMidiJavaCompiler.zip` einen, um die beiden neuen Bytecode-Befehle und zwei neue Operationen (`curAddr` und `fixUp`) erweiterten Code-Generator (`OpCode.java` und `CodeGenerator.java`) und eine erweiterte MidiJava-Maschine (`VirtualMachine.java`), welche die neuen Befehle ausführen kann. Sie müssen also nur mehr den lexikalischen Analysator um die neuen Schlüsselwörter (`if`, `else` und `while`) sowie den Compiler mit seinem Syntaxanalysator und semantischen Aktionen um die neuen Anweisungen erweitern. Verwenden Sie als Basis dazu folgenden Ausschnitt der ATG für MidiJava:
<!-- Attribute können nicht tiefgestellt werden und auch Pfeile lassen sich nicht einfach ergänzen: ~tiefgestellt~, <sub>tiefgestellt</sub>
Habe mit folgenden Sprachen experimentiert: java, ebnf, abnf, ada
 -->
```ebnf 
Stat = [ 
  ... /* empty stat (;), assignment and print here, new ones below */

  | "{" StatSeq 
    "}"

  | "if" "(" ident(out identStr)    sem String  id = scanner.identStr;
                                      boolean isDecl = symbolTable.isDecl(id);
                                      if (!isDecl)
                                        semanticError("variable not declared");
                                      else {
                                        codeGenerator.emit(OpCode.LOAD_VAL, 
                                          symbolTable.addrOf(id));
                                          codeGenerator.emit(OpCode.JMPZ, 0); // dummy
                                          addr = codeGenerator.curAddr() - 4;
                                      } // else
                                    endsem
    ")" Stat
    [ "else"                        sem codeGenerator.emit(OpCode.JMP, 0);
                                      codeGenerator.fixUp(addr, 
                                        codeGenerator.curAddr());
                                      addr = codeGenerator.curAddr() - 4;
                                    endsem
      Stat 
    ]	                            sem codeGenerator.fixUp(addr,
                                      codeGenerator.curAddr());
                                    endsem

  | "while" "(" ident(out identStr) sem String id = scanner.identStr;
                                      boolean isDecl = symbolTable.isDecl(id);
                                      if (!isDecl)
                                        semanticError("variable not declared");
                                      else {
                                        addr1 = codeGenerator.curAddr();
                                        codeGenerator.emit(OpCode.LOAD_VAL, 
                                          symbolTable.addrOf(id));
                                        codeGenerator.emit(OpCode.JMPZ, 0); // dummy
                                        addr2 = codeGenerator.curAddr() - 4;
                                      } // else
                                    endsem
    ")" Stat                        sem codeGenerator.emit(OpCode.JMP, addr1);
                                      codeGenerator.fixUp(addr2, 
                                        codeGenerator.curAddr());
                                    endsem
  ].
```

## 2. Optimierender MidiJava-Compiler

Arithmetische Ausdrücke kann man wie folgt durch Binärbäume darstellen: aus dem Operator wird der Wurzelknoten, aus dem linken Operanden der linke und aus dem rechten Operanden der rechte Teilbaum. Damit bekommt man einen sogenannten abstrakten Syntaxbaum (engl. *abstract syntax tree*, kurz AST). Sobald ein Ausdruck als AST im Hauptspeicher steht, ist es einfach, diesen mittels Baumdurchlauf (*in*-, *pre*- oder *postorder*), wieder in eine Textform (In-, Prä- oder Postfix-Notation) zu übersetzen.

Die Repräsentation von arithmetischen Ausdrücken als AST bietet aber auch die Möglichkeit, einfache Optimierungen in den MidiJava-Compiler einzubauen.

1. Ändern Sie die Erkennungsprozeduren für arithmetische Ausdrücke (`Expr`, `Term` und `Fact`) im Parser Ihres MidiJava-Compilers so ab, dass vorerst kein Code mehr für die Ausdrücke erzeugt, sondern ein AST aufgebaut wird, dessen Knoten Zeichenketten enthalten (die vier Operatoren, die Ziffernfolge einer Zahl oder den Bezeichner einer Variablen).

2. Erweitern Sie dann denn Codegenerator um eine Methode	
    ```java
    public void emitCodeForAST (AST t) {…}
    ```
    die aus dem AST in einem Postorder-Durchlauf Bytecode für die Berechnung des Ausdrucks durch die virtuelle MiniJava-Maschine erzeugt.

    **Beispiel**: Für den Ausdruck `(1 + 2) * 3` soll folgender AST aufgebaut werden:

    <!-- ![AST für `(1 + 2) * 3`](AST1.svg) -->
    <img src="ast1.svg" alt="AST für `... (17 + 4) ...`" width="20%">        

    Die Methode `emitCodeForAST` soll folgende Codesequenz erzeugen:

    ```ebnf
    LOAD_CONST 1
    LOAD_CONST 2
    ADD
    LOAD_CONST 3
    MUL
    ```
    Damit können Sie Ihren Compiler zwar schon testen – aber von Optimierung ist noch keine Rede. Die ASTs eignen sich aber dazu, einfache Optimierungen an Ausdrücken vorzunehmen, die z. B. in modernen Compilern eingesetzt werden: Die ASTs werden transformiert und erst die sich daraus ergebenden (kleineren) ASTs werden für die Codegenerierung herangezogen.

3. Eliminieren überflüssiger Rechenoperationen,
z. B.: `0 + expr` oder `expr + 0` oder `1 * expr` oder `expr * 1` oder `expr / 1` wird zu `expr`
oder in Baumform (für das erste Beispiel) dargestellt:

    <img src="ast2.svg" alt="AST für `0 + ...` -> `...`" width="50%">    

4. „Konstantenfaltung“, Berechnung konstanter Teilausdrücke,
z. B.: `... + 17 + 4 + ...` wird zu `... + 21 + ...`
    
    Versuchen Sie, möglichst viele solcher optimierenden AST-Transformationen zu implementieren und wenden Sie diese auf den AST an, solange sich dadurch Verkleinerungen ergeben.
    Durch diese Transformationen soll z. B. aus dem AST für `0 + (17 + 4) * 1` ein AST mit nur mehr dem Knoten `21` entstehen.

    <img src="ast3.svg" alt="AST für `17 + 4` -> `21`" width="50%">     

    Versuchen Sie, möglichst viele solcher optimierenden AST-Transformationen zu implementieren und auf den AST anzuwenden, solange sich dadurch Verkleinerungen ergeben.
    Durch diese Transformationen soll z. B. aus dem AST für `0 + (17 + 4) * 1` ein AST mit nur mehr dem Knoten `21` entstehen.


 
