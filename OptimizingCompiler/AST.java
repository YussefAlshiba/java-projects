/* AST:
   ---
   Abstract Syntax Tree node for arithmetic expressions.
   Each node holds a string value (operator, number, or identifier)
   and optional left and right children.
*/
public class AST {
    public String val;   // operator (+,-,*,/), number, or identifier
    public AST left;     // left child (null for leaves)
    public AST right;    // right child (null for leaves)

    // leaf node (number or identifier)
    public AST(String val) {
        this.val = val;
        this.left = null;
        this.right = null;
    }

    // inner node (operator with two children)
    public AST(String val, AST left, AST right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}
