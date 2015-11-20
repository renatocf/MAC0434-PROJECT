package Mips;

public class MipsTempMap implements Temp.TempMap {
  MipsFrame frame;

  public String tempMap(Temp.Temp t) {
    if (frame.tempMap(t) == null)
      return "$" + t.toString();
    else
      return frame.tempMap(t);
  }

  public MipsTempMap(MipsFrame f) {
    frame = f;
  }
}