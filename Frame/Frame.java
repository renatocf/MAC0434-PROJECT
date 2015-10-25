package Frame;
import java.util.List;

public abstract class Frame implements Temp.TempMap {
  public Temp.Label name;
  public List<Access> formals;
  public abstract Frame newFrame(Symbol.Symbol name, List<Boolean> formals);
  public abstract Access allocLocal(boolean escape);
  public abstract Temp.Temp FP();
  public abstract Temp.Temp RV();
  public abstract String toString();

  // novos
  public abstract String programTail();
}