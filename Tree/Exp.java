package Tree;
import Temp.Temp;
import java.util.LinkedList;
abstract public class Exp implements Hospitable {
    public abstract LinkedList<Exp> kids();
    public abstract Exp build(LinkedList<Exp> kids);
    public abstract void accept(IntVisitor v, int d);
    public abstract <R> R accept(ResultVisitor<R> v);
    public abstract Temp accept(CodeVisitor v);
}