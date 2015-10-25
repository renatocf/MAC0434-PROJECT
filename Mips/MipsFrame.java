package Mips;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.ListIterator;

import Symbol.Symbol;
import Temp.Temp;
import Temp.Label;
import Frame.Frame;
import Frame.Access;
import java.util.Arrays;

public class MipsFrame extends Frame {
  private static final int wordSize = 4;
  private int offset = 0;
  private static HashMap<Symbol,Integer> functions = new HashMap<Symbol,Integer>();
  private List<Access> actuals;

  private MipsFrame(Symbol n, List<Boolean> f) {
    Integer count = functions.get(n);
    if (count == null) {
      count = new Integer(0);
      name = new Label(n);
    } else {
      count = new Integer(count.intValue() + 1);
      name = new Label(n + "." + count);
    }
    functions.put(n, count);
    actuals = new LinkedList<Access>();
    formals = new LinkedList<Access>();
    int offset = 0;
    Iterator<Boolean> escapes = f.iterator();
    formals.add(allocLocal(escapes.next().booleanValue())); // primeiro argumento
    actuals.add(new InReg(V0));                             // vai no registrador V0

    for (int i = 0; i < argRegs.length; ++i) { // Tratar os argumentos que vao nos registradores (A0 - A3)
      if (!escapes.hasNext())
        break;
      offset += wordSize;

      formals.add(allocLocal(escapes.next().booleanValue()));
      actuals.add(new InReg(argRegs[i]));
    }
    while (escapes.hasNext()) { // Tratar o resto dos argumentos
      offset += wordSize;

      formals.add(allocLocal(escapes.next().booleanValue()));
      actuals.add(new InFrame(offset));
    }
  }

  public Frame newFrame(Symbol name, List<Boolean> formals) {
    if (this.name != null)
      name = Symbol.symbol(this.name + "." + name);
    return new MipsFrame(name, formals);
  }

  public Access allocLocal(boolean escape) {
    if (escape) {
      Access result = new InFrame(offset);
      offset -= wordSize;
      return result;
    } else
      return new InReg(new Temp());
  }

  static final Temp ZERO = new Temp(); // zero reg
  static final Temp AT = new Temp(); // reserved for assembler
  static final Temp V0 = new Temp(); // function result
  static final Temp V1 = new Temp(); // second function result
  static final Temp A0 = new Temp(); // argument1
  static final Temp A1 = new Temp(); // argument2
  static final Temp A2 = new Temp(); // argument3
  static final Temp A3 = new Temp(); // argument4
  static final Temp T0 = new Temp(); // caller-saved
  static final Temp T1 = new Temp();
  static final Temp T2 = new Temp();
  static final Temp T3 = new Temp();
  static final Temp T4 = new Temp();
  static final Temp T5 = new Temp();
  static final Temp T6 = new Temp();
  static final Temp T7 = new Temp();
  static final Temp S0 = new Temp(); // callee-saved
  static final Temp S1 = new Temp();
  static final Temp S2 = new Temp();
  static final Temp S3 = new Temp();
  static final Temp S4 = new Temp();
  static final Temp S5 = new Temp();
  static final Temp S6 = new Temp();
  static final Temp S7 = new Temp();
  static final Temp T8 = new Temp(); // caller-saved
  static final Temp T9 = new Temp();
  static final Temp K0 = new Temp(); // reserved for OS kernel
  static final Temp K1 = new Temp(); // reserved for OS kernel
  static final Temp GP = new Temp(); // pointer to global area
  static final Temp SP = new Temp(); // stack pointer
  static final Temp S8 = new Temp(); // callee-save (frame pointer)
  static final Temp RA = new Temp(); // return address

  // Register lists: must not overlap and must include every register that
  // might show up in code
  private static final Temp[]
    // registers dedicated to special purposes
    specialRegs = { ZERO, AT, K0, K1, GP, SP },
    // registers to pass outgoing arguments
    argRegs = { A0, A1, A2, A3 },
    // registers that a callee must preserve for its caller
    calleeSaves = { RA, S0, S1, S2, S3, S4, S5, S6, S7, S8 },
    // registers that a callee may use without preserving
    callerSaves = { T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, V0, V1 };

  static final Temp FP = new Temp(); // virtual frame pointer (eliminated)
  public Temp FP() { return FP; }
  public Temp RV() { return V0; }

  private static final
  HashMap<Temp,String> tempMap = new HashMap<Temp,String>(32);
  static {
    tempMap.put(ZERO, "$0");
    tempMap.put(AT,   "$at");
    tempMap.put(V0,   "$v0");
    tempMap.put(V1,   "$v1");
    tempMap.put(A0,   "$a0");
    tempMap.put(A1,   "$a1");
    tempMap.put(A2,   "$a2");
    tempMap.put(A3,   "$a3");
    tempMap.put(T0,   "$t0");
    tempMap.put(T1,   "$t1");
    tempMap.put(T2,   "$t2");
    tempMap.put(T3,   "$t3");
    tempMap.put(T4,   "$t4");
    tempMap.put(T5,   "$t5");
    tempMap.put(T6,   "$t6");
    tempMap.put(T7,   "$t7");
    tempMap.put(S0,   "$s0");
    tempMap.put(S1,   "$s1");
    tempMap.put(S2,   "$s2");
    tempMap.put(S3,   "$s3");
    tempMap.put(S4,   "$s4");
    tempMap.put(S5,   "$s5");
    tempMap.put(S6,   "$s6");
    tempMap.put(S7,   "$s7");
    tempMap.put(T8,   "$t8");
    tempMap.put(T9,   "$t9");
    tempMap.put(K0,   "$k0");
    tempMap.put(K1,   "$k1");
    tempMap.put(GP,   "$gp");
    tempMap.put(SP,   "$sp");
    tempMap.put(S8,   "$fp");
    tempMap.put(RA,   "$ra");
  }

  public String tempMap(Temp temp) {
    return tempMap.get(temp);
  }

    // Impressao dos frames

  private String regname(String reg) {
    HashMap<String,String> regnameMap = new HashMap<String,String>(32);
    regnameMap.put(ZERO.toString(), "$0");
    regnameMap.put(AT.toString(),   "$at");
    regnameMap.put(V0.toString(),   "$v0");
    regnameMap.put(V1.toString(),   "$v1");
    regnameMap.put(A0.toString(),   "$a0");
    regnameMap.put(A1.toString(),   "$a1");
    regnameMap.put(A2.toString(),   "$a2");
    regnameMap.put(A3.toString(),   "$a3");
    regnameMap.put(T0.toString(),   "$t0");
    regnameMap.put(T1.toString(),   "$t1");
    regnameMap.put(T2.toString(),   "$t2");
    regnameMap.put(T3.toString(),   "$t3");
    regnameMap.put(T4.toString(),   "$t4");
    regnameMap.put(T5.toString(),   "$t5");
    regnameMap.put(T6.toString(),   "$t6");
    regnameMap.put(T7.toString(),   "$t7");
    regnameMap.put(S0.toString(),   "$s0");
    regnameMap.put(S1.toString(),   "$s1");
    regnameMap.put(S2.toString(),   "$s2");
    regnameMap.put(S3.toString(),   "$s3");
    regnameMap.put(S4.toString(),   "$s4");
    regnameMap.put(S5.toString(),   "$s5");
    regnameMap.put(S6.toString(),   "$s6");
    regnameMap.put(S7.toString(),   "$s7");
    regnameMap.put(T8.toString(),   "$t8");
    regnameMap.put(T9.toString(),   "$t9");
    regnameMap.put(K0.toString(),   "$k0");
    regnameMap.put(K1.toString(),   "$k1");
    regnameMap.put(GP.toString(),   "$gp");
    regnameMap.put(SP.toString(),   "$sp");
    regnameMap.put(S8.toString(),   "$fp");
    regnameMap.put(RA.toString(),   "$ra");
    if (regnameMap.get(reg) == null)
      return reg;
    else
      return regnameMap.get(reg);
  }

  public String toString() {
    String txt = "--------------------\nFrame: " + name + "\nformals\tactuals\n";

    for (int i = 0; i < formals.size(); i++) {
      String f = formals.get(i).toString();
      String a = actuals.get(i).toString();
      txt += regname(f) + "\t" + regname(a) + "\n";
    }

    txt += "--------------------\n";

    return txt;
  }

  // NOVOS

  public Temp getArgReg(int i) {
    return argRegs[i];
  }

  public Temp getThisReg() {
    return V0;
  }

  public MipsFrame() {}

  //Mini Java Library will be appended to end of
    //program
    public String programTail(){

  return
      "         .text            \n" +
      "         .globl _halloc   \n" +
      "_halloc:                  \n" +
      "         li $v0, 9        \n" +
      "         syscall          \n" +
      "         j $ra            \n" +
      "                          \n" +
      "         .text            \n" +
      "         .globl _printint \n" +
      "_printint:                \n" +
      "         li $v0, 1        \n" +
      "         syscall          \n" +
      "         la $a0, newl     \n" +
      "         li $v0, 4        \n" +
      "         syscall          \n" +
      "         j $ra            \n" +
      "                          \n" +
      "         .data            \n" +
      "         .align   0       \n" +
      "newl:    .asciiz \"\\n\"  \n" +
      "         .data            \n" +
      "         .align   0       \n" +
      "str_er:  .asciiz \" ERROR: abnormal termination\\n\" "+
      "                          \n" +
      "         .text            \n" +
      "         .globl _error    \n" +
      "_error:                   \n" +
      "         li $v0, 4        \n" +
      "         la $a0, str_er   \n" +
      "         syscall          \n" +
      "         li $v0, 10       \n" +
      "         syscall          \n" ;
    }
}
