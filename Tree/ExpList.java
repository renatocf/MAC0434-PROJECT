package Tree;
import java.util.LinkedList;

public  class ExpList extends Exp  {

    private LinkedList<Exp> list;
    public ExpList(LinkedList<Exp> l){
  list = l;
    }
    public LinkedList<Exp> getList(){
  return list;
    }


    public LinkedList<Exp> kids(){return null;}
    public Exp build(LinkedList<Exp> kids){return null;}
    public void accept(IntVisitor v, int d){}
    public <R> R accept(ResultVisitor<R> v){return null;}
    public Temp.Temp accept(CodeVisitor v){return null;}

}
