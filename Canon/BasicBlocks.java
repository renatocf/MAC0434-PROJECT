package Canon;

import java.util.LinkedList;

public class BasicBlocks {
  public LinkedList<LinkedList<Tree.Stm>> blocks;
  public Temp.Label done;

  private LinkedList<Tree.Stm> currentBlock;

  void mkBlocks(LinkedList<Tree.Stm> stms) {
    if (stms.size() == 0)
      return;
    currentBlock = new LinkedList<Tree.Stm>();
    Tree.Stm s = stms.poll();
    if (!(s instanceof Tree.LABEL)) {
      currentBlock.addLast(new Tree.LABEL(new Temp.Label()));
    }
    currentBlock.addLast(s);
    while (stms.size() > 0) {
      s = stms.getFirst();
      if (s instanceof Tree.LABEL) {
        currentBlock.addLast(new Tree.JUMP(((Tree.LABEL)s).label));
        break;
      }
      currentBlock.addLast(stms.poll());
      if (s instanceof Tree.JUMP || s instanceof Tree.CJUMP)
        break;
    }
    if (stms.size() == 0)
      currentBlock.addLast(new Tree.JUMP(done));
    blocks.addLast((LinkedList<Tree.Stm>)currentBlock.clone());
    mkBlocks(stms);
  }

  public BasicBlocks(LinkedList<Tree.Stm> stms) {
    done = new Temp.Label();
    blocks = new LinkedList<LinkedList<Tree.Stm>>();
    mkBlocks(stms);
  }
}