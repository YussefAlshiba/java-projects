public class SymbolTable {

  private static final int MAX_SYMBOLS = 20;

  private static class VarInfo {
    public String ident;
    public int addr;

    VarInfo(String ident, int addr) {
      this.ident = ident;
      this.addr = addr;
    }
  }

  private final VarInfo[] st = new VarInfo[MAX_SYMBOLS];
  private int n = 0;

  private VarInfo varInfoFor(String ident) {
    int i = 0;
    while ((i < n) && !ident.equals(st[i].ident))
      i++;
    return (i < n) ? st[i] : null;
  }

  public boolean declVar(String ident) {
    if (varInfoFor(ident) == null) {
      st[n] = new VarInfo(ident, n);
      n++;
      return true;
    } else
      return false;
  }

  public boolean isDecl(String ident) {
    return varInfoFor(ident) != null;
  }

  public int addrOf(String ident) {
    return varInfoFor(ident).addr;
  }
}
