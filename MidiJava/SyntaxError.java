public class SyntaxError extends Error {
  public SyntaxError() {
  }
  public SyntaxError(String expected) {
    super(expected);
  }
}
