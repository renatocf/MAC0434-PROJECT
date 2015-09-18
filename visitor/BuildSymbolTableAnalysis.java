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
    List<PClassDecl> copy = new ArrayList<PClassDecl>(node.getClassDecl());
    for (PClassDecl e : copy) {
        e.apply(this);
    }
    setType(null);
  }

  @Override
  public void caseAMainClass(AMainClass node) {
    symbolTable.addClass(node.getName().toString(), null);
    currClass = symbolTable.getClass(node.getName().toString());

    currMethod = new Method("main", new AIdentifierType(new AIdentifier(new TId("void"))));
    currMethod.addVar(node.getMethodParameter().toString(), new AIdentifierType(new AIdentifier(new TId("String[]"))));
    node.getStatement().apply(this);

    currMethod = null;

    setType(null);
  }

  @Override
  public void caseASimpleClassDecl(ASimpleClassDecl node) {
       /* COMPLETAR */
  }

  @Override
  public void caseAExtendsClassDecl(AExtendsClassDecl node) {
    if (!symbolTable.addClass(node.getName().toString(), node.getParent().toString())) {
      System.out.println("Class " +  node.getName().toString() + "is already defined" );
      System.exit(-1);
    }
    currClass = symbolTable.getClass(node.getName().toString());
    for(PVariableDeclaration e : new ArrayList<PVariableDeclaration>(node.getVariables())) {
      e.apply(this);
    }
    for(PMethodDeclaration e : new ArrayList<PMethodDeclaration>(node.getMethods())) {
      e.apply(this);
    }
    setType(null);
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
  public void caseAIntArrayType(AIntArrayType node) {
    setType(node);
  }

  @Override
  public void caseABooleanType(ABooleanType node) {
      /* COMPLETAR */
  }

  @Override
  public void caseAIntType(AIntType node) {
      /* COMPLETAR */
  }

  @Override
  public void caseAIdentifierType(AIdentifierType node) {
      /* COMPLETAR */
  }

  @Override
  public void caseABlockStatement(ABlockStatement node) {
    for (PStatement e : new ArrayList<PStatement>(node.getStatements())) {
      e.apply(this);
    }
    setType(null);
  }
}
