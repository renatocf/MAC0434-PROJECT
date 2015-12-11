package RegAlloc;

import java.util.List;

import Assem.Instr;
import Temp.TempMap;

public class RegAlloc implements Temp.TempMap {
  public List<Assem.Instr> instrs;
  public Frame.Frame frame;

  public RegAlloc(Frame.Frame f, List<Assem.Instr> il) {
      instrs = il;
      frame = f;
  }

  public String tempMap(Temp.Temp temp) {
    return null;
  }
}

class Color implements TempMap {
  public Color(InterferenceGraph ig,
               TempMap initial,
               List<Temp.Temp> registers) {
  }

  public List<Temp.Temp> spills() {
    return null;
  }

  public String tempMap(Temp.Temp t) {
    return null;
  }
}
