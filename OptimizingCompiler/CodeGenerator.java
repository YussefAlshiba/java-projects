/* CodeGenerator:                                             HDO, 2025-05-01
   ------------
Code generator for the MidiJava compiler to collect bytecode.
============================================================================*/

import java.io.*;
import java.util.*;

public class CodeGenerator {

  private static final int MAX_CODE_LEN = 1000;

  private final byte[] ca = new byte[MAX_CODE_LEN]; // code array
  private int n = 0; // number of bytes in code array

  public CodeGenerator() {
    // nothing to do
  } // CodeGenerator

  public void emit(byte b) {
     ca[n++] = b; // b may be negative
  } // emit

  public void emit(int w) { // emit w >= 0 as four bytes
    assert w >= 0: "invalid negative integer";
    for (int i = 0; i < 4; i++) { // format: little endian
      emit((byte) (w % 256));
      w = w / 256;
    } // for
  } // emit

  public void emit(OpCode opc) {
    emit((byte)opc.ordinal()); // one byte
  } // emit

  public void emit(OpCode opc, int opd) {
    emit(opc); // one byte
    emit(opd); // four bytes
  } // emit

  /* BEGIN extensions for MidiJava */

  public int curAddr() {
    return n;
  } // curAddr

  public void fixUp(int addr, int opd) {
    int curN = n; // save current value of n
    n = addr;
    emit(opd);
    n = curN;     // restore n to its original value
  } // fixUp

  /* END extensions for MidiJava */

  public byte[] getCode() {
    return Arrays.copyOf(ca, n);
  } // getCode

  public void writeCode(String codeFileName) {
    try {
      FileOutputStream   fos = new FileOutputStream(codeFileName);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(Arrays.copyOf(ca, n));
      oos.close();
    } catch (IOException e) {
      throw new RuntimeException(e.getCause());
    } // catch
  } // writeCode

/* emitCodeForAST:
     Generates bytecode from AST using postorder traversal.
     Leaves emit LOAD_CONST or LOAD_VAL, inner nodes emit operators.
  */
  public void emitCodeForAST(AST t) {
    if (t == null) return;
    if (t.left == null && t.right == null) {
      // check if it's an identifier (contains @)
      if (t.val.contains("@")) {
        int addr = Integer.parseInt(t.val.split("@")[1]);
        emit(OpCode.LOAD_VAL, addr);
      } else {
        // it's a number
        emit(OpCode.LOAD_CONST, Integer.parseInt(t.val));
      }
    } else {
      emitCodeForAST(t.left);
      emitCodeForAST(t.right);
      switch (t.val) {
        case "+": emit(OpCode.ADD); break;
        case "-": emit(OpCode.SUB); break;
        case "*": emit(OpCode.MUL); break;
        case "/": emit(OpCode.DIV); break;
      }
    }
  }
  }
  // CodeGenerator
