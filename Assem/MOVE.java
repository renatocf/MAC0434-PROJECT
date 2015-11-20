package Assem;
import Temp.Temp;
import Temp.Label;
import Temp.TempMap;
import java.util.List;

public class MOVE extends Instr {
    public MOVE(String a, Temp d, Temp s) {
  assem = a;
  use = new Temp[]{s};
  def = new Temp[]{d};
  jumps = null;
    }
    public Temp dst() { return def[0]; }
    public Temp src() { return use[0]; }

    public String format(TempMap m) {
  if (m.tempMap(src()) == m.tempMap(dst()))
      return "#" + super.format(m);
  return super.format(m);
    }
}