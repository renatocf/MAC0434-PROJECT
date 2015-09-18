package visitor;

import minijava.analysis.*;
import minijava.node.*;

import java.util.*;

public class TypeCheckExpAnalysis extends DepthFirstAdapter {
  private PType type;

  public PType getType() {
    return type;
  }

  public void setType(PType t) {
    type = t;
  }

  @Override
  public void caseAAndExpression(AAndExpression node) {
    node.getLeft().apply(this);
    if (! (getType() instanceof ABooleanType) ) {
       System.out.println("Left side of And must be of type integer");
       System.exit(-1);
    }
    node.getRight().apply(this);
    if (! (getType() instanceof ABooleanType) ) {
       System.out.println("Right side of And must be of type integer");
       System.exit(-1);
    }
    setType(new ABooleanType());
  }

  @Override
  public void caseALessThanExpression(ALessThanExpression node) {
        /* COMPLETAR */
  }

  @Override
  public void caseAPlusExpression(APlusExpression node) {
        /* COMPLETAR */
  }

  @Override
  public void caseAMinusExpression(AMinusExpression node) {
        /* COMPLETAR */
  }

  @Override
  public void caseATimesExpression(ATimesExpression node) {
         /* COMPLETAR */
  }

  @Override
  public void caseAArrayLookupExpression(AArrayLookupExpression node) {
         /* COMPLETAR */
  }

  @Override
  public void caseAArrayLengthExpression(AArrayLengthExpression node) {
         /* COMPLETAR */
  }

  @Override
  public void caseACallExpression(ACallExpression node) {
        /* COMPLETAR */
  }

  @Override
  public void caseAIntegerExpression(AIntegerExpression node) {
    setType(new AIntType());
  }

  @Override
  public void caseATrueExpression(ATrueExpression node) {
         /* COMPLETAR */
  }

  @Override
  public void caseAFalseExpression(AFalseExpression node) {
          /* COMPLETAR */
  }

  @Override
  public void caseAIdentifierExpression(AIdentifierExpression node) {
          /* COMPLETAR */
  }

  @Override
  public void caseAThisExpression(AThisExpression node) {
          /* COMPLETAR */
  }

  @Override
  public void caseANewArrayExpression(ANewArrayExpression node) {
         /* COMPLETAR */
  }

  @Override
    public void caseANewObjectExpression(ANewObjectExpression node) {
         /* COMPLETAR */
  }

  @Override
  public void caseANotExpression(ANotExpression node) {
        /* COMPLETAR */
  }
}
