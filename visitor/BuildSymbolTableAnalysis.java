package visitor;

import minijava.analysis.*;
import minijava.node.*;

import java.util.*;

public class BuildSymbolTableAnalysis extends DepthFirstAdapter {
  SymbolTable symbolTable;

  private Class currClass;
  private Method currMethod;
  private PType type;

  public BuildSymbolTableAnalysis() {
    symbolTable = new SymbolTable();
  }

  public SymbolTable getSymTab() {
    return symbolTable;
  }

  public void setType(PType t) {
    type = t;
  }

  public PType getType() {
    return type;
  }

  @Override
  public void caseAProgram(AProgram node) {
    node.getMainClass().apply(this);
    for (PClassDecl e : node.getClassDecl()) {
      e.apply(this);
    }
    setType(null);
  }

  @Override
  public void caseAMainClass(AMainClass node) {
    String className = node.getName().toString();

    symbolTable.addClass(className, null);
    currClass = symbolTable.getClass(className);

    currMethod = new Method(
      "main", new AIdentifierType(new AIdentifier(new TId("void"))));
    currMethod.addVar(
      node.getMethodParameter().toString(),
      new AIdentifierType(new AIdentifier(new TId("String[]"))));

    node.getStatement().apply(this);
    currMethod = null;

    setType(null);
  }

  @Override
  public void caseASimpleClassDecl(ASimpleClassDecl node) {
    String className = node.getName().toString();

    if (!symbolTable.addClass(className, null))
      error(node, "Class " + className + "is already defined" );

    currClass = symbolTable.getClass(className);
    for(PVariableDeclaration e : node.getVariables()) {
      e.apply(this);
    }
    for(PMethodDeclaration e : node.getMethods()) {
      e.apply(this);
    }
    setType(null);
  }

  @Override
  public void caseAExtendsClassDecl(AExtendsClassDecl node) {
    String className = node.getName().toString();

    if (!symbolTable.addClass(className, node.getParent().toString()))
      error(node, "Class " + className + "is already defined" );

    currClass = symbolTable.getClass(className);
    for(PVariableDeclaration e : node.getVariables()) {
      e.apply(this);
    }
    for(PMethodDeclaration e : node.getMethods()) {
      e.apply(this);
    }
    setType(null);
  }

  @Override
  public void caseAVariableDeclaration(AVariableDeclaration node) {
    String varName = node.getName().toString();

    node.getType().apply(this);

    if ((currMethod == null && !currClass.addVar(varName, getType()))
    ||  (currMethod != null && !currMethod.addVar(varName, getType())))
      error(node, "Variable " + varName + "is already defined" );

    setType(null);
  }

  @Override
  public void caseAMethodDeclaration(AMethodDeclaration node) {
    String methodName = node.getName().toString();

    node.getReturnType().apply(this);
    if (!currClass.addMethod(methodName, getType()))
      error(node, "Method " + methodName + "is already defined" );

    currMethod = currClass.getMethod(methodName);

    for(PFormalParameter e : node.getFormals()) {
      e.apply(this);
    }
    for(PVariableDeclaration e : node.getLocals()) {
      e.apply(this);
    }
    for(PStatement e : node.getStatements()) {
      e.apply(this);
    }

    currMethod = null;
    setType(null);
  }

  @Override
  public void caseAFormalParameter(AFormalParameter node) {
    String paramName = node.getName().toString();

    node.getType().apply(this);
    if (!currMethod.addParam(paramName, getType()))
      error(node, "Method " + paramName + "is already defined" );

    setType(null);
  }

  @Override
  public void caseAIntArrayType(AIntArrayType node) {
    setType(node);
  }

  @Override
  public void caseABooleanType(ABooleanType node) {
    setType(node);
  }

  @Override
  public void caseAIntType(AIntType node) {
    setType(node);
  }

  @Override
  public void caseAIdentifierType(AIdentifierType node) {
    setType(node);
  }

  @Override
  public void caseABlockStatement(ABlockStatement node) {
    for (PStatement e : node.getStatements()) {
      e.apply(this);
    }
    setType(null);
  }

  // Auxiliar methods
  private void error(Node node, String msg) {
    System.err.println(msg);

    System.err.println();
    System.err.println("Error on AST subtree:");
    node.apply(new PrettyPrinter());
    System.exit(-1);
  }
}
