package visitor;
import Symbol.Symbol;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

public class Level {
    public Level parent;
    Frame.Frame frame;    // not public!
    public List<Access> formals = new LinkedList<Access>();
    public Level(Level p, Symbol name, List<Boolean> escapes) {
      parent = p;
      frame = parent.frame.newFrame(name, escapes);
      for (Iterator<Frame.Access> f = frame.formals.iterator(); f.hasNext();)
        formals.add(new Access(this, f.next()));
    }

    Level(Frame.Frame f) { frame = f; }

    public Temp.Label name() { return frame.name; }

    public Access allocLocal(boolean escape) {
      return new Access(this, frame.allocLocal(escape));
    }
}

