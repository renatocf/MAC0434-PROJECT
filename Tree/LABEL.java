package Tree;
import Temp.Temp;
import Temp.Label;
import java.util.LinkedList;
public class LABEL extends Stm {
    public Label label;
    public LABEL(Label l) { label=l; }
    public LinkedList<Exp> kids() { return new LinkedList<Exp>(); }
    public Stm build(LinkedList<Exp> kids) { return this; }
    public void accept(IntVisitor v, int d) { v.visit(this, d); }
    public void accept(CodeVisitor v) { v.visit(this); }
    public <R> R accept(ResultVisitor<R> v) { return v.visit(this); }
}