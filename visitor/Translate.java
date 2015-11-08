package visitor;

import minijava.analysis.*;
import minijava.node.*;
import Temp.Temp;
import Temp.Label;
import Temp.Offset;
import Symbol.*;
import Frame.*;
import java.util.*;

public class Translate extends DepthFirstAdapter {

  private SymbolTable symbolTable;
  private Frame frame;

  public Translate(Frame f, SymbolTable s) {
    frame = f;
    symbolTable = s;
  }

  private LinkedList<Frag> frags = new LinkedList<Frag>();

  public Iterator<Frag> getResults() {
    return frags.iterator();
  }

  private Frame currentFrame;

  public void procEntryExit(Exp body) {
    Tree.Exp bodyExp = body.unEx();
    Tree.Stm bodyStm;
    if (bodyExp != null)
      bodyStm = MOVE(TEMP(currentFrame.RV()), bodyExp);
    else
      bodyStm = body.unNx();
    ProcFrag frag = new ProcFrag(bodyStm, currentFrame);
    frags.add(frag);
  }

  private Exp currentExpression;
  private Method currentMethod;
  private Class currentClass;

  @Override
  public void caseAProgram(AProgram node) {
    node.getMainClass().apply(this);
    DataFrag frag = new DataFrag(frame.programTail());
    frags.add(frag);
    List<PClassDecl> copy = new ArrayList<PClassDecl>(node.getClassDecl());
    for(PClassDecl e : copy) {
      e.apply(this);
    }
    currentExpression = null;
  }

  @Override
  public void caseAMainClass(AMainClass node) {
    String className = node.getName().toString().replaceAll("\\s+","");
    currentClass = symbolTable.getClass(className);

    currentMethod = currentClass.getMethod("Main");

    Symbol sym =  Symbol.symbol("main");
    LinkedList<Boolean> params = new LinkedList<Boolean>();
    params.add(0, new Boolean(false)); // (String[] argv)
    // params.add(0, new Boolean(true)); // we add the static link here
    currentFrame = frame.newFrame(sym, params);

    procEntryExit(visitAndGetExp(node.getStatement()));

    currentExpression = null;
    currentMethod = null;
    currentClass = null;
  }

  @Override
  public void caseAPrintlnStatement(APrintlnStatement node) {
    Exp e = visitAndGetExp(node.getValue());
    LinkedList<Tree.Exp> args1 = new LinkedList<Tree.Exp>();
    args1.add(e.unEx());
    currentExpression = new Nx(MOVE(TEMP(new Temp()), CALL(NAME(new Label("_printint")), args1)));
  }

  @Override
  public void caseAIntegerExpression(AIntegerExpression node) {
    int value = Integer.parseInt(node.getValue().toString().replaceAll("\\s+",""));
    currentExpression = new Ex(CONST(value));
  }

  @Override
  public void caseASimpleClassDecl(ASimpleClassDecl node) {
    String className = node.getName().toString().replaceAll("\\s+","");
    Class klass = symbolTable.getClass(className);
    List<PMethodDeclaration> copy = new ArrayList<PMethodDeclaration>(node.getMethods());
    for(PMethodDeclaration e : copy) {
      currentClass = klass;
      e.apply(this);
    }
    currentClass = null;
    currentExpression = null;
  }

  @Override
  public void caseAVariableDeclaration(AVariableDeclaration node) {
    if (currentMethod != null) {
      Variable v = currentMethod.getVar(node.getName().toString().replaceAll("\\s+", ""));
      v.setAccess(currentFrame.allocLocal(false));
    }
  }

  @Override
  public void caseAMethodDeclaration(AMethodDeclaration node) {
    String className = currentClass.getId();
    String metName = node.getName().toString().replaceAll("\\s+","");
    String metLabel = className + "_" + metName;
    int arg_number = 0 ;

    Method met = currentClass.getMethod(metName);
    currentMethod = met;
    arg_number = met.numberOfParams() + 1; // + this

    LinkedList<Boolean> params = new LinkedList<Boolean>();
    Symbol sym =  Symbol.symbol(metLabel);

    for (int i = 0; i < arg_number; i++)
      params.add(new Boolean(false));

    // params.add(0, new Boolean(true)); // we add the static link here
    currentFrame = frame.newFrame(sym, params);
    met.setAccess(currentFrame.formals);

    List<PVariableDeclaration> locals = new ArrayList<PVariableDeclaration>(node.getLocals());
    for(PVariableDeclaration e : locals)
    {
      e.apply(this);
    }

    Tree.Stm stms;
    List<PStatement> e = new ArrayList<PStatement>(node.getStatements());

    //empty block
    if (e.size() == 0){
      stms = null;
      Exp retexp = visitAndGetExp(node.getReturnExpression());
      procEntryExit(new Ex(ESEQ(stms,retexp.unEx())));
      currentExpression = null;
      return;
    }

    Exp exp = visitAndGetExp(e.get(0));
    stms = exp.unNx();

    boolean firstStm = true;
    for (PStatement elem : e) {
      exp = visitAndGetExp(elem);
      if (firstStm) {
        stms = exp.unNx();
        firstStm = false;
      }
      else {
        stms = SEQ(stms, exp.unNx());
      }
    }

    Exp retexp = visitAndGetExp(node.getReturnExpression());
    procEntryExit(new Ex(ESEQ(stms,retexp.unEx())));

    currentMethod = null;
    currentExpression = null;
    return;
  }

  private Exp getIdentifierExp(String varName) {
    if (currentMethod != null){
      Variable v = null;
      if (currentMethod.containsVar(varName)) {
        v = currentMethod.getVar(varName);
      } else if (currentMethod.containsParam(varName)) {
        v = currentMethod.getParam(varName);
      } else if (currentClass.containsVar(varName)) {
        v = currentClass.getVar(varName);
        Tree.Exp objectAddress = currentFrame.formals.get(0).exp(TEMP(currentFrame.FP()));
        Tree.Exp varAddress = MEM(BINOP(Tree.BINOP.PLUS, objectAddress, CONST(4*(1 + v.getIndex()))));
        return new Ex(varAddress);
      }
      return new Ex(v.getAccess().exp(TEMP(currentFrame.FP())));
    }

    return null;
  }

  @Override
  public void caseAIdentifierExpression(AIdentifierExpression node) {
    currentExpression = getIdentifierExp(node.getName().toString().replaceAll("\\s+",""));
  }

  @Override
  public void caseAThisExpression(AThisExpression node) {
    currentExpression = new Ex(currentFrame.formals.get(0).exp(TEMP(currentFrame.FP())));
  }

  @Override
  public void caseAIdentifier(AIdentifier node) {
    currentExpression = getIdentifierExp(node.getName().toString().replaceAll("\\s+",""));
  }

  @Override
  public void caseAAssignStatement(AAssignStatement node) {
    Exp lvalue = visitAndGetExp(node.getName());
    Exp rvalue = visitAndGetExp(node.getValue());
    currentExpression = new Nx(MOVE (lvalue.unEx(),  rvalue.unEx()));
  }

  @Override
  public void caseAPlusExpression(APlusExpression node) {
    Exp expl = visitAndGetExp(node.getLeft());
    Exp expr = visitAndGetExp(node.getRight());
    currentExpression = new Ex(BINOP
        (Tree.BINOP.PLUS,
         expl.unEx(),
         expr.unEx()));
  }

  @Override
  public void caseAMinusExpression(AMinusExpression node) {
    Exp expl = visitAndGetExp(node.getLeft());
    Exp expr = visitAndGetExp(node.getRight());
    currentExpression = new Ex(BINOP
        (Tree.BINOP.MINUS,
         expl.unEx(),
         expr.unEx()));
  }

  @Override
  public void caseATimesExpression(ATimesExpression node) {
    Exp expl = visitAndGetExp(node.getLeft());
    Exp expr = visitAndGetExp(node.getRight());
    currentExpression = new Ex(BINOP
        (Tree.BINOP.MUL,
         expl.unEx(),
         expr.unEx()));
  }

  @Override
  public void caseAAndExpression(AAndExpression node) {
    Temp t1 = new Temp();
    Label done = new Label();
    Label ok1 = new Label();
    Label ok2 = new Label();
    Tree.Exp left = visitAndGetExp(node.getLeft()).unEx();
    Tree.Exp right = visitAndGetExp(node.getRight()).unEx();

    currentExpression = new Ex(ESEQ
        (SEQ
         (SEQ
          (SEQ
           (SEQ(SEQ(MOVE(TEMP(t1),CONST(0)),
                    CJUMP(Tree.CJUMP.EQ, left, CONST(1), ok1, done)),
                SEQ(LABEL(ok1),
                  CJUMP(Tree.CJUMP.EQ, right, CONST(1), ok2, done))),
            SEQ(LABEL(ok2),  MOVE(TEMP(t1),CONST(1)))),
           JUMP(done)),
          LABEL(done)),
         TEMP(t1)));
  }

  @Override
  public void caseALessThanExpression(ALessThanExpression node) {
    Temp t1 = new Temp();
    Label F = new Label();
    Label done = new Label();
    Tree.Exp left = visitAndGetExp(node.getLeft()).unEx();
    Tree.Exp right = visitAndGetExp(node.getRight()).unEx();

    currentExpression = new Ex(ESEQ
        (SEQ
         (SEQ
          (SEQ
           (SEQ(MOVE(TEMP(t1),CONST(1)),
                CJUMP(Tree.CJUMP.GE, left, right, F, done)),
            SEQ(LABEL(F), MOVE(TEMP(t1),CONST(0)))),
           JUMP(done)),
          LABEL(done)),
         TEMP(t1)));
  }

  @Override
  public void caseANotExpression(ANotExpression node) {
    Exp e = visitAndGetExp(node.getExpression());
    currentExpression = new Ex
      (BINOP(Tree.BINOP.MINUS, CONST(1),
             e.unEx()));
  }

  @Override
  public void caseATrueExpression(ATrueExpression node) {
    currentExpression = new Ex(CONST(1));
  }

  @Override
  public void caseAFalseExpression(AFalseExpression node) {
    currentExpression = new Ex(CONST(0));
  }

  @Override
  public void caseAIfStatement(AIfStatement node) {
    Label T = new Label();
    Label F = new Label();
    Label D = new Label();
    Exp exp = visitAndGetExp(node.getCondition());
    Exp stmT = visitAndGetExp(node.getTrueStatement());
    Exp stmF = visitAndGetExp(node.getFalseStatement());
    currentExpression = new Nx(SEQ
        (SEQ
         (SEQ
          (SEQ
           (CJUMP(Tree.CJUMP.EQ,exp.unEx(),CONST(1),T,F),
            SEQ(LABEL(T),stmT.unNx())),
           JUMP(D)),
          SEQ(LABEL(F),stmF.unNx())),
         LABEL(D)));
  }

  @Override
  public void caseAWhileStatement(AWhileStatement node) {
    Label L = new Label();
    Label T = new Label();
    Label D = new Label();
    Exp exp = visitAndGetExp(node.getCondition());
    Exp stm = visitAndGetExp(node.getStatement());
    currentExpression = new Nx(SEQ
        (SEQ
         (SEQ
          (SEQ(LABEL(L),CJUMP(Tree.CJUMP.EQ,exp.unEx(),CONST(1),T,D)),
           SEQ(LABEL(T),stm.unNx())),
          JUMP(L)),
         LABEL(D)));

    //    (SEQ
    //     (SEQ
    //      (LABEL(L),
    //       SEQ(CJUMP(Tree.CJUMP.EQ,exp.unEx(),CONST(1),L,D),stm.unNx())),
    //      JUMP(L)),
    //     LABEL(D)));
  }

  @Override
  public void caseABlockStatement(ABlockStatement node) {
    List<PStatement> stms = new ArrayList<PStatement>(node.getStatements());
    Tree.Stm stm = null;
    boolean firstStm = true;
    for(PStatement e : stms) {
      Exp exp = visitAndGetExp(e);
      if (firstStm) {
        firstStm = false;
        if (stms.size() == 1) {
          currentExpression = exp;
          return;
        }
        stm = exp.unNx();
      } else {
        stm = SEQ(stm, exp.unNx());
      }
    }
    currentExpression = new Nx(stm);
  }

  @Override
  public void caseANewArrayExpression(ANewArrayExpression node) {
    Temp t1 = new Temp();
    Temp t2 = new Temp();
    Label cj = new Label();
    Label F = new Label();
    Label T = new Label();

    Exp expSize = visitAndGetExp(node.getSize());

    Tree.Exp size =BINOP(Tree.BINOP.MUL,BINOP(Tree.BINOP.PLUS,expSize.unEx(),CONST(1)),CONST(4));

    // 1. call _halloc get pointer to space allocated in t1
    LinkedList<Tree.Exp> args1 = new LinkedList<Tree.Exp>();
    args1.add(size);
    Tree.Stm s1 = MOVE(TEMP(t1), CALL(NAME(new Label("_halloc")),args1));

    // 2.Initialization
    Tree.Stm s2 =
      SEQ
      (SEQ
       (SEQ
        (SEQ
         (SEQ
          (SEQ
           (MOVE(TEMP(t2),CONST(4)),
            SEQ (LABEL(cj),CJUMP(Tree.CJUMP.LT,TEMP(t2),size,F,T))),
           LABEL(T)),
          MOVE(MEM(BINOP(Tree.BINOP.PLUS,TEMP(t1),TEMP(t2))),CONST(0))),
         MOVE(TEMP(t2),BINOP(Tree.BINOP.PLUS,TEMP(t2),CONST(4)))),
        JUMP(cj)),
       SEQ(LABEL(F),MOVE(MEM(TEMP(t1)),BINOP(Tree.BINOP.MUL,expSize.unEx(),CONST(4)))));

    currentExpression = new Ex(ESEQ(SEQ(s1,s2),TEMP(t1)));
  }

  @Override
  public void caseAArrayAssignStatement(AArrayAssignStatement node) {
    Tree.Exp e1 = visitAndGetExp(node.getName()).unEx();
    if (!(e1 instanceof Tree.TEMP)) {
      Temp taux1= new Temp();
      Temp taux2= new Temp();
      e1 = ESEQ(MOVE(TEMP(taux1), e1), TEMP(taux1));
    }

    Tree.Exp e2 = visitAndGetExp(node.getIndex()).unEx();

    Temp t = new Temp();
    Temp t_index = new Temp();
    Temp t_size = new Temp();
    LinkedList<Tree.Exp> args1 = new LinkedList<Tree.Exp>();
    Label T = new Label();
    Label F = new Label();

    e2 = ESEQ
      (SEQ
       (SEQ
        (SEQ
         (SEQ
          (SEQ
           (MOVE(TEMP(t_index),
                 BINOP(Tree.BINOP.MUL,e2,CONST(4))),
            MOVE(TEMP(t_size),MEM(e1))),
           CJUMP(Tree.CJUMP.GE,TEMP(t_index),TEMP(t_size),T,F)),
          LABEL(T)),
         MOVE(TEMP(new Temp()),
           CALL(NAME(new Label("_error")),args1))),
        LABEL(F)),
       TEMP(t_index));

    Tree.Exp e3 = visitAndGetExp(node.getValue()).unEx();

    currentExpression = new Nx
      (MOVE
       (MEM
        (BINOP
         (Tree.BINOP.PLUS,e1,BINOP
          (Tree.BINOP.PLUS,e2,CONST(4)))),
        e3));
  }

  @Override
  public void caseAArrayLookupExpression(AArrayLookupExpression node) {
    Temp t_index = new Temp();
    Temp t_size = new Temp();
    Tree.Exp e1 = visitAndGetExp(node.getArray()).unEx();
    Tree.Exp e2 = visitAndGetExp(node.getIndex()).unEx();

    Label F = new Label();
    Label T = new Label();

    LinkedList<Tree.Exp> args1 = new LinkedList<Tree.Exp>();

    Tree.Stm s1 = SEQ
      (SEQ
       (SEQ
        (SEQ
         (SEQ
          (MOVE(TEMP(t_index),BINOP(Tree.BINOP.MUL,e2,CONST(4))),
           MOVE(TEMP(t_size),MEM(e1))),
          CJUMP(Tree.CJUMP.GE,TEMP(t_index),TEMP(t_size),T,F)),
         LABEL(T)),
        MOVE(TEMP(new Temp()),
          CALL(NAME(new Label("_error")),args1))),
       LABEL(F));

    Temp t = new Temp();
    Tree.Stm s2 = SEQ
      (s1,MOVE(TEMP(t),MEM
               (BINOP(Tree.BINOP.PLUS,e1,BINOP
                      (Tree.BINOP.PLUS,
                       BINOP(Tree.BINOP.MUL,e2,CONST(4)),
                       CONST(4))))));

    currentExpression = new Ex(ESEQ(s2,TEMP(t)));
  }

  @Override
  public void caseANewObjectExpression(ANewObjectExpression node) {
    Class klass = symbolTable.getClass(node.getClassName().toString());
    Temp t1 = new Temp();
    Temp t2 = new Temp();
    Label cj = new Label();
    Label F = new Label();
    Label T = new Label();

    // Tree.Exp size = BINOP(Tree.BINOP.MUL,
    //     BINOP(Tree.BINOP.PLUS,
    //       CONST(klass.getNumberOfVars()),
    //       CONST(1)),
    //     CONST(4));

    System.out.println("Num vars: " + klass.getNumberOfVars());
    Tree.Exp size = CONST((klass.getNumberOfVars() + 1) * 4);

    // 1. call _halloc get pointer to space allocated in t1
    LinkedList<Tree.Exp> args = new LinkedList<Tree.Exp>();
    args.add(size);
    Tree.Stm s1 = MOVE(TEMP(t1), CALL(NAME(new Label("_halloc")), args));

    // 2.Initialization
    Tree.Stm s2 =
      SEQ
      (SEQ
       (SEQ
        (SEQ
         (SEQ
          (SEQ
           (MOVE(TEMP(t2),CONST(0)),
            SEQ (LABEL(cj),CJUMP(Tree.CJUMP.LT,TEMP(t2),size,F,T))),
           LABEL(T)),
          MOVE(MEM(BINOP(Tree.BINOP.PLUS,TEMP(t1),TEMP(t2))),CONST(0))),
         MOVE(TEMP(t2),BINOP(Tree.BINOP.PLUS,TEMP(t2),CONST(4)))),
        JUMP(cj)),
       SEQ(LABEL(F),MOVE(MEM(TEMP(t1)),BINOP(Tree.BINOP.MUL,CONST(klass.getNumberOfVars()),CONST(4)))));

    currentExpression = new Ex(ESEQ(SEQ(s1,s2),TEMP(t1)));
  }

  @Override
  public void caseACallExpression(ACallExpression node) {
    // Completar aqui

    TypeCheckExpAnalysis analyzer =
      new TypeCheckExpAnalysis(new TypeCheckAnalysis(currentClass, currentMethod, symbolTable));
    node.getInstance().apply(analyzer);

    String className = analyzer.getType().toString().replaceAll("\\s+","");
    String methodName = node.getName().toString().replaceAll("\\s+","");

    Tree.Exp instance = visitAndGetExp(node.getInstance()).unEx();
    symbolTable.getClass();

    LinkedList<Tree.Exp> args = new LinkedList<Tree.Exp>();
    args.add(instance);

    for (PExpression actual : node.getActuals()) {
        args.add(visitAndGetExp(actual).unEx());
    }

    currentExpression = new Ex
      (CALL(NAME(new Label(className + "_" + methodName)), args));
  }

  private static Tree.Exp CONST(int value)  { return new Tree.CONST(value); }
  private static Tree.Exp NAME(Label label) {  return new Tree.NAME(label); }
  private static Tree.Exp TEMP(Temp temp)   {  return new Tree.TEMP(temp); }
  private static Tree.Exp BINOP(int binop, Tree.Exp left, Tree.Exp right) {
    return new Tree.BINOP(binop, left, right);
  }
  private static Tree.Exp MEM(Tree.Exp exp) {  return new Tree.MEM(exp); }
  private static Tree.Exp CALL(Tree.Exp func, List<Tree.Exp> args) {
    return new Tree.CALL(func, args);
  }
  private static Tree.Exp ESEQ(Tree.Stm stm, Tree.Exp exp) {
    if (stm == null) return exp;
    return new Tree.ESEQ(stm, exp);
  }
  private static Tree.Stm MOVE(Tree.Exp dst, Tree.Exp src) {
    return new Tree.MOVE(dst, src);
  }
  private static Tree.Stm EXP(Tree.Exp exp) {  return new Tree.EXPR(exp); }
  private static Tree.Stm JUMP(Label target) {
    return new Tree.JUMP(target);
  }
  private static Tree.Stm CJUMP(int relop, Tree.Exp l, Tree.Exp r, Label t,
      Label f) {
    return new Tree.CJUMP(relop, l, r, t, f);
  }
  private static Tree.Stm SEQ(Tree.Stm left, Tree.Stm right) {
    if (left == null)
      return right;
    if (right == null)
      return left;
    return new Tree.SEQ(left, right);
  }
  private static Tree.Stm LABEL(Label label) {
    return new Tree.LABEL(label);
  }

  private Exp visitAndGetExp(Node node) {
    node.apply(this);
    return currentExpression;
  }

}
