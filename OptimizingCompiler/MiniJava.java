import java.io.*;

public class MiniJava {

  public static void main(String[] args) {

    String         sourceFileName   = null;
    BufferedReader sourceFileReader = null;

    IO.println("MidiJava V. 0 (2026)\n");
    do {
      IO.print("source file name > ");
      sourceFileName = IO.readln();
      try {
         sourceFileReader = new BufferedReader(new FileReader(sourceFileName));
      } catch(IOException e) {
        IO.println("error: source file not found\n");
      }
    } while (sourceFileReader == null);

    Scanner scanner = new Scanner(sourceFileReader);

    IO.print("mode (parse, interpret or compile) > ");
    char mode = IO.readln().charAt(0);
    try {
      switch (mode) {

        case 'p':
          Parser parser = new Parser(scanner);
          parser.parse();
          break;

        case 'c':
          CodeGenerator codeGenerator = new CodeGenerator();
          Compiler compiler = new Compiler(scanner, codeGenerator);
          compiler.compile();

          byte[] codeArray = codeGenerator.getCode();
          IO.println(codeArray.length + " bytes of code generated");
          Decoder decoder = new Decoder(codeArray);
          decoder.decode();

          VirtualMachine virtualMachine1 = new VirtualMachine(codeArray);
          virtualMachine1.run();

          int    dotPos       = sourceFileName.indexOf('.');
          String codeFileName = sourceFileName.substring(0, dotPos) + ".mjc";
          IO.println("storing code in   file " + codeFileName);
          codeGenerator.writeCode(codeFileName);
          IO.println("reading code from file " + codeFileName);
          VirtualMachine virtualMachine2 = new VirtualMachine(codeFileName);
          virtualMachine2.run();
          break;

        default:
          IO.println("error: invalid mode, only p or c allowed");

      }
    } catch (SyntaxError se) {
      IO.println("syntax error in line " + scanner.syLnr +
           ", column " + scanner.syCnr + ": " + se.getMessage());
      System.exit(1);
    }
    IO.println("\nsource file processed successfully");

  }

}
