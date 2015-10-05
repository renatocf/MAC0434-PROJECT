package visitor;

import minijava.analysis.*;
import minijava.node.*;

import java.util.*;

public class TypeCheckAnalysis extends DepthFirstAdapter {
  static Class currClass;
  static Method currMethod;
  static SymbolTable symbolTable;

  public TypeCheckAnalysis(SymbolTable s){
    symbolTable = s;
  }

  // Getters
  public Class getCurrClass() {
    return currClass;
  }

  public Method getCurrMethod() {
    return currMethod;
  }

  public SymbolTable getSymbolTable() {
    return symbolTable;
  }

  @Override
  public void caseAProgram(AProgram node) {
    node.getMainClass().apply(this);
    for (PClassDecl e : node.getClassDecl()) {
      e.apply(this);
    }
  }

  @Override
  public void caseAMainClass(AMainClass node) {
    String className = node.getName().toString();
    currClass = symbolTable.getClass(className);

    node.getMethodParameter().apply(this);
    node.getStatement().apply(this);
  }

  @Override
  public void caseASimpleClassDecl(ASimpleClassDecl node) {
    String className = node.getName().toString();
    currClass = symbolTable.getClass(className);

    for(PVariableDeclaration e : node.getVariables()) {
      e.apply(this);
    }
    for(PMethodDeclaration e : node.getMethods()) {
      e.apply(this);
    }

    currClass = null;
  }

  @Override
  public void caseAExtendsClassDecl(AExtendsClassDecl node) {
    String className = node.getName().toString();
    currClass = symbolTable.getClass(className);

    node.getParent().apply(this);
    for(PVariableDeclaration e : node.getVariables()) {
      e.apply(this);
    }
    for(PMethodDeclaration e : node.getMethods()) {
      e.apply(this);
    }

    currClass = null;
  }

  @Override
  public void caseAVariableDeclaration(AVariableDeclaration node) {
    PType type = node.getType();

    if (! (type instanceof AIntType)
    &&  ! (type instanceof ABooleanType)
    &&  ! (type instanceof AIntArrayType)
    &&  ! (type instanceof AIdentifierType)
    &&  ! (symbolTable.containsClass(((AIdentifierType) type).getName().toString())) )
      error(node, "Variable type " + type.toString() + " not declared");
  }

  @Override
  public void caseAMethodDeclaration(AMethodDeclaration node) {
    String methodName = node.getName().toString();
    currMethod = symbolTable.getMethod(methodName, currClass.getId());

    TypeCheckExpAnalysis v = new TypeCheckExpAnalysis(this);
    node.getReturnExpression().apply(v);

    if (!isValidAssignment(node.getReturnType(), v.getType())) {
      error(node, "Wrong return type on method " + currMethod.getId());
    }

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
  }

  @Override
  public void caseAFormalParameter(AFormalParameter node) {
    PType type = node.getType();

    if (! (type instanceof AIntType)
    &&  ! (type instanceof ABooleanType)
    &&  ! (type instanceof AIntArrayType)
    &&  ! (type instanceof AIdentifierType)
    &&  ! (symbolTable.containsClass(((AIdentifierType) type).getName().toString())) )
      error(node, "Parameter type " + node.getType().toString() + " not declared");
  }

  @Override
  public void caseAIfStatement(AIfStatement node) {
    TypeCheckExpAnalysis v = new TypeCheckExpAnalysis(this);
    node.getCondition().apply(v);

    if (! (v.getType() instanceof ABooleanType) )
      error(node, "The condition of an if must be of type boolean");

    node.getTrueStatement().apply(this);
    node.getFalseStatement().apply(this);
  }

  @Override
  public void caseAWhileStatement(AWhileStatement node) {
    TypeCheckExpAnalysis v = new TypeCheckExpAnalysis(this);
    node.getCondition().apply(v);

    if (! (v.getType() instanceof ABooleanType) )
       error(node, "The condition of a while must be of type boolean");

    node.getStatement().apply(this);
  }

  @Override
  public void caseAPrintlnStatement(APrintlnStatement node) {
    TypeCheckExpAnalysis v = new TypeCheckExpAnalysis(this);
    node.getValue().apply(v);

    if (! (v.getType() instanceof AIntType)
    &&  ! (v.getType() instanceof ABooleanType) )
       error(node, "A System.out.println can only print booleans and ints");
  }

  @Override
  public void caseAAssignStatement(AAssignStatement node) {
    String lvalName = node.getName().toString();
    if (!containsVariable(lvalName)) {
      error(node, "Variable " + node.getName().toString() + " not declared");
    }

    PType lvalType = findVariable(lvalName).type();

    TypeCheckExpAnalysis vRval = new TypeCheckExpAnalysis(this);
    node.getValue().apply(vRval);

    PType rvalType = vRval.getType();

    if (!isValidAssignment(lvalType, rvalType))
      error(node, "Incompatible types on assignment");
  }

  @Override
  public void caseAArrayAssignStatement(AArrayAssignStatement node) {
    TypeCheckExpAnalysis vIndex = new TypeCheckExpAnalysis(this);
    node.getIndex().apply(vIndex);
    if (! (vIndex.getType() instanceof AIntType) )
       error(node, "Index must be of type integer");

    String lvalName = node.getName().toString();
    if (!containsVariable(lvalName)) {
      error(node, "Variable " + node.getName().toString() + " not declared");
    }

    PType lvalType = findVariable(lvalName).type();

    TypeCheckExpAnalysis vRval = new TypeCheckExpAnalysis(this);
    node.getValue().apply(vRval);

    PType rvalType = vRval.getType();

    if (!isValidArrayAssignment(lvalType, rvalType))
      error(node, "Incompatible types on assignment");
  }

  // Auxiliar methods
  public boolean containsVariable(String varName) {
    return findVariable(varName) != null;
  }

  public Variable findVariable(String varName) {
    if (currMethod.containsVar(varName)) {
      return currMethod.getVar(varName);
    }
    else if (currMethod.containsParam(varName)) {
      return currMethod.getParam(varName);
    }
    else {
      return findParameter(varName, currClass.getId());
    }
  }

  public boolean containsParameter(String paramName, String className) {
    return findParameter(paramName, className) != null;
  }

  public Variable findParameter(String varName, String className) {
    if (className == null) return null;

    Class currClass = symbolTable.getClass(className);
    if (currClass.containsVar(varName))
      return currClass.getVar(varName);

    return findParameter(varName, currClass.parent());
  }

  public boolean containsMethod(String methodName, String className) {
    return findMethod(methodName, className) != null;
  }

  public Method findMethod(String methodName, String className) {
    if (className == null) return null;

    Class calledClass = symbolTable.getClass(className);
    if (calledClass.containsMethod(methodName))
      return calledClass.getMethod(methodName);

    return findMethod(methodName, calledClass.parent());
  }

  public boolean isValidAssignment(PType assignedType, PType assigneeType) {
    if (! (assignedType instanceof AIntType      && assigneeType instanceof AIntType)
    &&  ! (assignedType instanceof ABooleanType  && assigneeType instanceof ABooleanType)
    &&  ! (assignedType instanceof AIntArrayType && assigneeType instanceof AIntArrayType)) {
      if (! (assignedType instanceof AIdentifierType
          && assigneeType instanceof AIdentifierType)) return false;
      return isTypeCompatible(symbolTable.getClass(assignedType.toString()),
                              symbolTable.getClass(assigneeType.toString()));
    }
    return true;
  }

  public boolean isValidArrayAssignment(PType assignedType, PType assigneeType) {
    if (! (assignedType instanceof AIntType      && assigneeType instanceof AIntType)
    &&  ! (assignedType instanceof ABooleanType  && assigneeType instanceof ABooleanType)
    &&  ! (assignedType instanceof AIntArrayType && assigneeType instanceof AIntType)) {
      if (! (assignedType instanceof AIdentifierType
          && assigneeType instanceof AIdentifierType)) return false;
      return isTypeCompatible(symbolTable.getClass(assignedType.toString()),
                              symbolTable.getClass(assigneeType.toString()));
    }
    return true;
  }

  private boolean isTypeCompatible(Class assigned, Class assignee) {
    if (assignee.type().equals(assigned.type())) return true;
    if (assignee.parent() != null) {
        return isTypeCompatible(assigned, symbolTable.getClass(assignee.parent()));
    }
    return false;
  }

  private void error(Node node, String msg) {
    System.err.println(msg.replaceAll("\\s+", " "));

    System.err.println();
    System.err.println("Error on AST subtree:");
    node.apply(new PrettyPrinter());
    System.exit(-1);
  }
}
