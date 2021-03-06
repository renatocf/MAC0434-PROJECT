package visitor;

import minijava.analysis.*;
import minijava.node.*;

import java.util.*;

public class TypeCheckExpAnalysis extends DepthFirstAdapter {
  private TypeCheckAnalysis stmChecker;
  private PType type;

  public TypeCheckExpAnalysis(TypeCheckAnalysis stmChecker) {
      this.stmChecker = stmChecker;
  }

  public PType getType() {
    return type;
  }

  public void setType(PType t) {
    type = t;
  }

  @Override
  public void caseAAndExpression(AAndExpression node) {
    node.getLeft().apply(this);
    if (! (getType() instanceof ABooleanType))
       error(node, "Left side of and must be of type boolean");

    node.getRight().apply(this);
    if (! (getType() instanceof ABooleanType))
       error(node, "Right side of and must be of type boolean");

    setType(new ABooleanType());
  }

  @Override
  public void caseALessThanExpression(ALessThanExpression node) {
    node.getLeft().apply(this);
    if (! (getType() instanceof AIntType))
       error(node, "Left side of less must be of type integer");

    node.getRight().apply(this);
    if (! (getType() instanceof AIntType))
       error(node, "Right side of less must be of type integer");

    setType(new ABooleanType());
  }

  @Override
  public void caseAPlusExpression(APlusExpression node) {
    node.getLeft().apply(this);
    if (! (getType() instanceof AIntType))
       error(node, "Left side of plus must be of type integer");

    node.getRight().apply(this);
    if (! (getType() instanceof AIntType))
       error(node, "Right side of plus must be of type integer");

    setType(new AIntType());
  }

  @Override
  public void caseAMinusExpression(AMinusExpression node) {
    node.getLeft().apply(this);
    if (! (getType() instanceof AIntType))
       error(node, "Left side of minus must be of type integer");

    node.getRight().apply(this);
    if (! (getType() instanceof AIntType))
       error(node, "Right side of minus must be of type integer");

    setType(new AIntType());
  }

  @Override
  public void caseATimesExpression(ATimesExpression node) {
    node.getLeft().apply(this);
    if (! (getType() instanceof AIntType))
       error(node, "Left side of times must be of type integer");

    node.getRight().apply(this);
    if (! (getType() instanceof AIntType))
       error(node, "Right side of times must be of type integer");

    setType(new AIntType());
  }

  @Override
  public void caseAArrayLookupExpression(AArrayLookupExpression node) {
    node.getArray().apply(this);
    if (! (getType() instanceof AIntArrayType))
       error(node, "Array in Array lookup must be of type int array");

    node.getIndex().apply(this);
    if (! (getType() instanceof AIntType))
       error(node, "Index must be of type integer");

    setType(new AIntType());
  }

  @Override
  public void caseAArrayLengthExpression(AArrayLengthExpression node) {
    node.getArray().apply(this);
    if (! (getType() instanceof AIntArrayType))
       error(node, "Array in length must be of type int array");

    setType(new AIntType());
  }

  @Override
  public void caseACallExpression(ACallExpression node) {
    node.getInstance().apply(this);

    String className = getType().toString();
    String methodName = node.getName().toString();

    if (!stmChecker.containsMethod(methodName, className)) {
      error(node, "Method " + methodName + " not defined");
    }

    Method calledMethod = stmChecker.findMethod(methodName, className);

    Enumeration params = calledMethod.getParams();
    for (PExpression actual : node.getActuals()) {
        if (!params.hasMoreElements()) {
          error(node, "Excess of parameters on call of " + calledMethod.getId());
        }

        actual.apply(this);
        Variable param = (Variable) params.nextElement();

        if (!stmChecker.isValidAssignment(param.type(), getType()))
           error(node, "Mismatched type " + getType()
                        + " on parameter " + param
                        + " on method " + calledMethod.getId());
    }

    if (params.hasMoreElements()) {
      error(node, "Wrong number of parameters on call of " + calledMethod.getId());
    }

    setType(calledMethod.type());
  }

  @Override
  public void caseAIntegerExpression(AIntegerExpression node) {
    setType(new AIntType());
  }

  @Override
  public void caseATrueExpression(ATrueExpression node) {
    setType(new ABooleanType());
  }

  @Override
  public void caseAFalseExpression(AFalseExpression node) {
    setType(new ABooleanType());
  }

  @Override
  public void caseAIdentifierExpression(AIdentifierExpression node) {
    setType(stmChecker.getSymbolTable().getVarType(
      stmChecker.getCurrMethod(),
      stmChecker.getCurrClass(),
      node.getName().toString()));
  }

  @Override
  public void caseAThisExpression(AThisExpression node) {
    setType(stmChecker.getCurrClass().type());
  }

  @Override
  public void caseANewArrayExpression(ANewArrayExpression node) {
    setType(new AIntArrayType());
  }

  @Override
  public void caseANewObjectExpression(ANewObjectExpression node) {
    String className = node.getClassName().toString();

    if (!stmChecker.getSymbolTable().containsClass(className))
     error(node, "Class " + className + " not defined");

    setType(stmChecker.getSymbolTable().getClass(className).type());
  }

  @Override
  public void caseANotExpression(ANotExpression node) {
    node.getExpression().apply(this);
    if (! (getType() instanceof ABooleanType))
     error(node, "Expression in a not must be of type boolean");
  }

  // Auxiliar methods
  private void error(Node node, String msg) {
    System.err.println(msg.replaceAll("\\s+", " "));

    System.err.println();
    System.err.println("Error on AST subtree:");
    node.apply(new PrettyPrinter());
    System.exit(-1);
  }
}
