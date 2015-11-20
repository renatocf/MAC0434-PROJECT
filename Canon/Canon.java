package Canon;

import java.util.LinkedList;

class MoveCall extends Tree.Stm {
  Tree.TEMP dst;
  Tree.CALL src;
  MoveCall(Tree.TEMP d, Tree.CALL s) {dst=d; src=s;}
  public LinkedList<Tree.Exp> kids() {return src.kids();}
  public Tree.Stm build(LinkedList<Tree.Exp> kids) { return new Tree.MOVE(dst, src.build(kids)); }
  public void accept(Tree.IntVisitor v, int d) { throw new Error("accept() not applicable"); }
  public void accept(Tree.CodeVisitor v) { throw new Error("accept() not applicable"); }
  public <R> R accept(Tree.ResultVisitor<R> v) { throw new Error("accept() not applicable"); }
}

class ExpCall extends Tree.Stm {
  Tree.CALL call;
  ExpCall(Tree.CALL c) {call=c;}
  public LinkedList<Tree.Exp> kids() {return call.kids();}
  public Tree.Stm build(LinkedList<Tree.Exp> kids) { return new Tree.EXPR(call.build(kids)); }
  public void accept(Tree.IntVisitor v, int d) { throw new Error("accept() not applicable"); }
  public void accept(Tree.CodeVisitor v) { throw new Error("accept() not applicable"); }
  public <R> R accept(Tree.ResultVisitor<R> v) { throw new Error("accept() not applicable"); }
}

class StmExpList {
  Tree.Stm stm;
  LinkedList<Tree.Exp> exps;
  StmExpList(Tree.Stm s, LinkedList<Tree.Exp> e) {stm=s; exps=e;}
}

public class Canon {

  static boolean isNop(Tree.Stm a) {
   return a instanceof Tree.EXPR && ((Tree.EXPR)a).exp instanceof Tree.CONST;
  }

  static Tree.Stm seq(Tree.Stm a, Tree.Stm b) {
    if (isNop(a))
      return b;
    else if (isNop(b))
      return a;
    else
      return new Tree.SEQ(a,b);
  }

  static boolean commute(Tree.Stm a, Tree.Exp b) {
    return isNop(a) || b instanceof Tree.NAME || b instanceof Tree.CONST;
  }

  static Tree.Stm do_stm(Tree.SEQ s) {
    return seq(do_stm(s.left), do_stm(s.right));
  }

  static Tree.Stm do_stm(Tree.MOVE s) {
    if (s.dst instanceof Tree.TEMP && s.src instanceof Tree.CALL)
      return reorder_stm(new MoveCall((Tree.TEMP)s.dst, (Tree.CALL)s.src));
    else if (s.dst instanceof Tree.ESEQ)
      return do_stm(new Tree.SEQ(((Tree.ESEQ)s.dst).stm, new Tree.MOVE(((Tree.ESEQ)s.dst).exp, s.src)));
    else
      return reorder_stm(s);
  }

  static Tree.Stm do_stm(Tree.EXPR s) {
    if (s.exp instanceof Tree.CALL)
      return reorder_stm(new ExpCall((Tree.CALL)s.exp));
    else
      return reorder_stm(s);
  }

  static Tree.Stm do_stm(Tree.Stm s) {
    if (s instanceof Tree.SEQ)
      return do_stm((Tree.SEQ)s);
    else if (s instanceof Tree.MOVE)
      return do_stm((Tree.MOVE)s);
    else if (s instanceof Tree.EXPR)
      return do_stm((Tree.EXPR)s);
    else
      return reorder_stm(s);
  }

  static Tree.Stm reorder_stm(Tree.Stm s) {
    StmExpList x = reorder(s.kids());
    return seq(x.stm, s.build(x.exps));
  }

  static Tree.ESEQ do_exp(Tree.ESEQ e) {
    Tree.Stm stms = do_stm(e.stm);
    Tree.ESEQ b = do_exp(e.exp);
    return new Tree.ESEQ(seq(stms,b.stm), b.exp);
  }

  static Tree.ESEQ do_exp (Tree.Exp e) {
    if (e instanceof Tree.ESEQ)
      return do_exp((Tree.ESEQ)e);
    else
      return reorder_exp(e);
  }

  static Tree.ESEQ reorder_exp (Tree.Exp e) {
    StmExpList x = reorder(e.kids());
    return new Tree.ESEQ(x.stm, e.build(x.exps));
  }

  static StmExpList nopNull = new StmExpList(new Tree.EXPR(new Tree.CONST(0)), new LinkedList<Tree.Exp>());

  static StmExpList reorder(LinkedList<Tree.Exp> exps) {
    if (exps.size() == 0) {
      return nopNull;
    } else {
      Tree.Exp a = exps.getFirst();
      if (a instanceof Tree.CALL) {
        Temp.Temp t = new Temp.Temp();
        Tree.Exp e = new Tree.ESEQ(new Tree.MOVE(new Tree.TEMP(t), a), new Tree.TEMP(t));
        LinkedList<Tree.Exp> list = (LinkedList<Tree.Exp>)exps.clone();
        list.removeFirst();
        list.addFirst(e);
        return reorder(list);
      } else {
        Tree.ESEQ aa = do_exp(a);
        LinkedList<Tree.Exp> list1 = (LinkedList<Tree.Exp>)exps.clone();
        list1.removeFirst();
        StmExpList bb = reorder(list1);
        if (commute(bb.stm, aa.exp)) {
          LinkedList<Tree.Exp> list = (LinkedList<Tree.Exp>)bb.exps.clone();
          list.addFirst(aa.exp);
          return new StmExpList(seq(aa.stm,bb.stm), list);
        } else {
          Temp.Temp t = new Temp.Temp();
          LinkedList<Tree.Exp> list = (LinkedList<Tree.Exp>)bb.exps.clone();
          list.addFirst(new Tree.TEMP(t));
          return new StmExpList(seq(aa.stm, seq(new Tree.MOVE(new Tree.TEMP(t),aa.exp), bb.stm)), list);
        }
      }
    }
  }

  static LinkedList<Tree.Stm> linear(Tree.SEQ s, LinkedList<Tree.Stm> l) {
    return linear(s.left,linear(s.right,l));
  }

  static LinkedList<Tree.Stm> linear(Tree.Stm s, LinkedList<Tree.Stm> l) {
    if (s instanceof Tree.SEQ) {
      return linear((Tree.SEQ)s, l);
    }
    else {
      LinkedList<Tree.Stm> list = (LinkedList<Tree.Stm>)l.clone();
      list.addFirst(s);
      return list;
    }
  }

  static public LinkedList<Tree.Stm> linearize(Tree.Stm s) {
    return linear(do_stm(s), new LinkedList<Tree.Stm>());
  }
}