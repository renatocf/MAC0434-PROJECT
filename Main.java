import java.io.*;
import java.util.ArrayList;
import java.util.List;

import Mips.MipsFrame;
import Frame.Frame;
import Symbol.Symbol;

public class Main {
  public static void main(String[] arguments) {
    ArrayList formals1 = new ArrayList<Boolean>();
    formals1.add(true);
    formals1.add(true);
    formals1.add(true);
    formals1.add(false);
    formals1.add(true);
    formals1.add(false);
    formals1.add(false);
    formals1.add(false);
    formals1.add(false);
    formals1.add(false);
    Frame frame1 = new MipsFrame(Symbol.symbol("A"), formals1);
    System.out.println(frame1.toString());

    ArrayList formals2 = new ArrayList<Boolean>();
    formals2.add(false);
    formals2.add(false);
    formals2.add(true);
    formals2.add(false);
    formals2.add(true);
    Frame frame2 = frame1.newFrame(Symbol.symbol("B"), formals2);
    System.out.println(frame2.toString());
  }
}
