/* OpCode:                                                   HDO, 2025-05-01
   ------
Operation codes for MidiJava bytecode.
============================================================================*/

public enum OpCode {
  LOAD_CONST, // operand num: numerical literal
  LOAD_VAL,   // operand addr: address of variable for value to load
  LOAD_ADDR,  // operand addr: address of variable
  STORE,
  ADD, SUB, MUL, DIV,
  READ, PRINT, END,
  JMP, JMPZ   // MidiJava
} // OpCode
