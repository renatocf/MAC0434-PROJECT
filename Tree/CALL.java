package Tree;
import Temp.Temp;
import Temp.Label;
import java.util.List;
import java.util.LinkedList;
public class CALL extends Exp {
    public Exp func;
    public List<Exp> args;
    public CALL(Exp f, List<Exp> a) { func=f; args=a; }
    public LinkedList<Exp> kids() {
  LinkedList<Exp> kids = new LinkedList<Exp>();
  kids.addFirst(func);
  kids.addAll(args);
  return kids;
    }
    public Exp build(LinkedList<Exp> kids) {
  return new CALL(kids.removeFirst(), kids);
    }
    public void accept(IntVisitor v, int d) { v.visit(this, d); }
    public Temp accept(CodeVisitor v) { return v.visit(this); }
    public <R> R accept(ResultVisitor<R> v) { return v.visit(this); }
}