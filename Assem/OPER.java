package Assem;
import Temp.Temp;
import Temp.Label;
import java.util.List;

public class OPER extends Instr {
    public OPER(String a, Temp[] d, Temp[] s, List<Label> j) {
  assem = a;
  use = s;
  def = d;
  jumps = j;
    }
}