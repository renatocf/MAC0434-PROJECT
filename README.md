# Capítulo 9

- Adicionar os pacotes Assem e Canon

- Adicionar Mips/Codegen.java e MipsTempMap.java

- Modificar o arquivo Mips/MipsFrame.java. Adicione:

```
  public int wordSize() { return wordSize; }

  private static <R> void addAll(java.util.Collection<R> c, R[] a) {
    for (int i = 0; i < a.length; i++)
      c.add(a[i]);
  }

  int maxArgOffset = 0;

  // Registers live on return
  private static Temp[] returnSink = {};
  {
    LinkedList<Temp> l = new LinkedList<Temp>();
    l.add(V0);
    addAll(l, specialRegs);
    addAll(l, calleeSaves);
    returnSink = l.toArray(returnSink);
  }

  // Registers defined by a call
  static Temp[] calldefs = {};
  {
    LinkedList<Temp> l = new LinkedList<Temp>();
    l.add(RA);
    addAll(l, argRegs);
    addAll(l, callerSaves);
    calldefs = l.toArray(calldefs);
  }

  private static Tree.Stm SEQ(Tree.Stm left, Tree.Stm right) {
    if (left == null)
      return right;
    if (right == null)
      return left;
    return new Tree.SEQ(left, right);
  }

  private static Tree.MOVE MOVE(Tree.Exp d, Tree.Exp s) {
    return new Tree.MOVE(d, s);
  }

  private static Tree.TEMP TEMP(Temp t) {
    return new Tree.TEMP(t);
  }

  private void assignFormals(Iterator<Access> formals, Iterator<Access> actuals, List<Tree.Stm> body) {
    if (!formals.hasNext() || !actuals.hasNext())
        return;
    Access formal = formals.next();
    Access actual = actuals.next();
    assignFormals(formals, actuals, body);
    body.add(0, MOVE(formal.exp(TEMP(FP)), actual.exp(TEMP(FP))));
  }

  private void assignCallees(int i, List<Tree.Stm> body) {
    if (i >= calleeSaves.length)
        return;
    Access a = allocLocal(!spilling);
    assignCallees(i+1, body);
    body.add(0, MOVE(a.exp(TEMP(FP)), TEMP(calleeSaves[i])));
    body.add(MOVE(TEMP(calleeSaves[i]), a.exp(TEMP(FP))));
  }

  public void procEntryExit1(List<Tree.Stm> body) {
    assignFormals(formals.iterator(), actuals.iterator(), body);
    assignCallees(0, body);
  }

  private static Assem.Instr OPER(String a, Temp[] d, Temp[] s) {
    return new Assem.OPER(a, d, s, null);
  }

  public void procEntryExit2(List<Assem.Instr> body) {
    body.add(OPER("#\treturn", null, returnSink));
  }

  public void procEntryExit3(List<Assem.Instr> body) {

  }

  private static boolean spilling = true ;

  // set spilling to true when the spill method is implemented
  public void spill(List<Assem.Instr> insns, Temp[] spills) {
    if (spills != null) {
      if (!spilling) {
        for (int s = 0; s < spills.length; s++)
          System.err.println("Need to spill " + spills[s]);
          throw new Error("Spilling unimplemented");
        }
      else for (int s = 0; s < spills.length; s++) {
        Tree.Exp exp = allocLocal(true).exp(TEMP(FP));
          for (ListIterator<Assem.Instr> i = insns.listIterator(); i.hasNext(); ) {
            Assem.Instr insn = i.next();
            Temp[] use = insn.use;
            if (use != null)
            for (int u = 0; u < use.length; u++) {
              if (use[u] == spills[s]) {
                Temp t = new Temp();
                t.spillTemp = true;
                Tree.Stm stm = MOVE(TEMP(t), exp);
                i.previous();
                stm.accept(new Codegen(this, i));
                if (insn != i.next())
                  throw new Error();
                insn.replaceUse(spills[s], t);
                break;
              }
            }
            Temp[] def = insn.def;
            if (def != null)
              for (int d = 0; d < def.length; d++) {
                if (def[d] == spills[s]) {
                  Temp t = new Temp();
                  t.spillTemp = true;
                  insn.replaceDef(spills[s], t);
                  Tree.Stm stm = MOVE(exp, TEMP(t));
                  stm.accept(new Codegen(this, i));
                  break;
                }
              }
          }
      }
    }
  }
```

- Você também pode utilizar o Main.java para ver como o visitor percorre a IR para gerar instruções.

- Complete o visitor Mips/Codegen.java

# Links Úteis

- SPIM: Simulador Mips -> http://spimsimulator.sourceforge.net

Para utilizar o simulador, crie um arquivo .asm. Este arquivo deve conter um label `main:` que será o ponto de entrada do seu programa. Exemplo:

```
main:
  li   $t1 123
  move $v0 $t1
  jal  _printint
  move $t2 $v0
  li   $t1 321
  move $v0 $t1
  jal  _printint
  move $t2 $v0
  jal  _end

_printint:
  move $a0 $v0
  li   $v0 1
  syscall
  li   $a0 10
  li   $v0 11
  syscall
  jr   $ra

_end:
  li   $v0 10
  syscall
```

Este programa deve imprimir 123 e 321 no console. Para executá-lo:

```
1) "File" -> "Reinitialize and Load File"
2) Escolha o seu arquivo .asm
3) "Simulator" -> "Run/Continue"
```

Lembre-se que o assembly que você gerará não usará os registradores reais do MIPS. Portanto, por enquanto, você terá que fazer algumas adaptações para testar seus programas no SPIM.

- Introdução ao Assembly usando o Simulador SPIM -> http://cee.uma.pt/people/faculty/pedro.campos/docs/guia-AC.pdf

