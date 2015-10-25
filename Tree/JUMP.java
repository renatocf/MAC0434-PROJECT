package Tree;
import java.util.List;
import java.util.LinkedList;
public class JUMP extends Stm {
    public Exp exp;
    public LinkedList<Temp.Label> targets;
    public JUMP(Exp e, LinkedList<Temp.Label> t) { exp=e; targets=t; }
    public JUMP(Temp.Label target) {
  exp = new NAME(target);
  targets = new LinkedList<Temp.Label>();
  targets.addFirst(target);
    }
    public LinkedList<Exp> kids() {
  LinkedList<Exp> kids = new LinkedList<Exp>();
  kids.addFirst(exp);
  return kids;
    }
    public Stm build(LinkedList<Exp> kids) {
  return new JUMP(kids.getFirst(), targets);
    }
    public void accept(IntVisitor v, int d) { v.visit(this, d); }
    public void accept(CodeVisitor v) { v.visit(this); }
    public <R> R accept(ResultVisitor<R> v) { return v.visit(this); }
}