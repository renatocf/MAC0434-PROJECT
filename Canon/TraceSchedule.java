package Canon;

import java.util.LinkedList;
import java.util.HashMap;

public class TraceSchedule {

  public LinkedList<LinkedList<LinkedList<Tree.Stm>>> traces = new LinkedList<LinkedList<LinkedList<Tree.Stm>>>();
  private LinkedList<LinkedList<Tree.Stm>> currentTrace;
  private LinkedList<LinkedList<Tree.Stm>> blocks;
  BasicBlocks basicBlocks;
  HashMap<String, LinkedList<Tree.Stm>> table = new HashMap<String, LinkedList<Tree.Stm>>();

  private void buildDictionary() {
    blocks = (LinkedList<LinkedList<Tree.Stm>>)basicBlocks.blocks.clone();
    for (LinkedList<Tree.Stm> block : blocks) {
      table.put(((Tree.LABEL)(block.getFirst())).label.toString(), block);
    }
  }

  private void trace(int i) {
    if (blocks.size() <= i)
      return;
    currentTrace = new LinkedList<LinkedList<Tree.Stm>>();
    LinkedList<Tree.Stm> block = blocks.get(i);
    while (block != null) {
      Tree.LABEL first = (Tree.LABEL)block.getFirst();
      Tree.Stm last = block.getLast();
      if (table.get(first.label.toString()) == null)
        break;
      table.put(first.label.toString(), null);
      currentTrace.addLast(block);
      if (last instanceof Tree.JUMP) {
        String label = ((Tree.NAME)(((Tree.JUMP)last).exp)).label.toString();
        block = table.get(label);
      } else if (last instanceof Tree.CJUMP) {
        String label = ((Tree.CJUMP)last).iffalse.toString();
        block = table.get(label);
      }
    }
    if (currentTrace.size() > 0)
      traces.addLast(currentTrace);
    trace(i+1);
  }

  public TraceSchedule(BasicBlocks b) {
    basicBlocks = b;
    buildDictionary();
    trace(0);
  }
}

