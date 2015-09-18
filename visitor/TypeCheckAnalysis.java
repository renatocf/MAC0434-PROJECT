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

  @Override
  public void caseAProgram(AProgram node) {
    node.getMainClass().apply(this);
    List<PClassDecl> copy = new ArrayList<PClassDecl>(node.getClassDecl());
    for (PClassDecl e : copy) {
        e.apply(this);
    }
  }

  @Override
  public void caseAMainClass(AMainClass node) {
    String i1 = node.getName().toString();
    currClass = symbolTable.getClass(i1);

    node.getMethodParameter().apply(this);
    node.getStatement().apply(this);
  }

  @Override
  public void caseASimpleClassDecl(ASimpleClassDecl node) {
       /* COMPLETAR */
  }

  @Override
  public void caseAExtendsClassDecl(AExtendsClassDecl node) {
    String id = node.getName().toString();
    currClass = symbolTable.getClass(id);
    node.getParent().apply(this);
    for(PVariableDeclaration e : new ArrayList<PVariableDeclaration>(node.getVariables())) {
      e.apply(this);
    }
    for(PMethodDeclaration e : new ArrayList<PMethodDeclaration>(node.getMethods())) {
      e.apply(this);
    }
  }

  @Override
  public void caseAVariableDeclaration(AVariableDeclaration node) {
        /* COMPLETAR */
  }

  @Override
  public void caseAMethodDeclaration(AMethodDeclaration node) {
        /* COMPLETAR */
  }

  @Override
  public void caseAFormalParameter(AFormalParameter node) {
        /* COMPLETAR */
  }

  @Override
  public void caseAIfStatement(AIfStatement node) {
    TypeCheckExpAnalysis v = new TypeCheckExpAnalysis();
    node.getCondition().apply(v);
    if (! (v.getType() instanceof ABooleanType) ) {
       System.out.println("The condition of while must be of type boolean");
       System.exit(-1);
    }
    node.getTrueStatement().apply(this);
    node.getFalseStatement().apply(this);
  }

  @Override
  public void caseAWhileStatement(AWhileStatement node) {
        /* COMPLETAR */
  }

  @Override
  public void caseAPrintlnStatement(APrintlnStatement node) {
        /* COMPLETAR */
  }

  @Override
  public void caseAAssignStatement(AAssignStatement node) {
        /* COMPLETAR */
  }

  @Override
  public void caseAArrayAssignStatement(AArrayAssignStatement node) {
        /* COMPLETAR */
  }

}
