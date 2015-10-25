//package Translate;
package visitor;
import Temp.Label;

public class Nx extends Exp {
    Tree.Stm stm;
    Nx(Tree.Stm s) { stm = s; }

    Tree.Exp unEx() { return null; }

    Tree.Stm unNx() { return stm; }

    Tree.Stm unCx(Label t, Label f) { return null; }
}