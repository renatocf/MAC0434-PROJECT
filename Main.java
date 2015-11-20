import minijava.parser.*;
import minijava.lexer.*;
import minijava.node.*;
import visitor.*;
import Mips.*;
import java.io.*;
import java.util.*;
import Assem.Instr;

public class Main {
  public static void main(String[] arguments) {
    try {
      // Create a lexer instance.
      Lexer lexer = new Lexer(new PushbackReader(new InputStreamReader(System.in), 1024));
      Parser parser = new Parser(lexer);
      Start ast = parser.parse();

      System.out.println("Construindo a tabela de simbolos...");

      //Build the symbol table
      BuildSymbolTableAnalysis vb = new BuildSymbolTableAnalysis();
      ast.apply(vb);

      System.out.println("Analisando tipos...");

      //Type checking
      TypeCheckAnalysis vt = new TypeCheckAnalysis(vb.getSymTab());
      ast.apply(vt);

      System.out.println("Traduzindo...");

      Translate translate = new Translate(new MipsFrame() ,vb.getSymTab());
      ast.apply(translate);

      for (Iterator<visitor.Frag> frags = translate.getResults(); frags.hasNext(); ) {
        //get next fragment
        Frag f = frags.next();

        //if the fragment is a ProcFrag i.e one which contains a procedure
        //then I get the map of temps associated with it and print it out.

        if (f instanceof ProcFrag) {
          ProcFrag proc = (ProcFrag)f;
          Temp.TempMap tempmap = new Temp.CombineMap(proc.frame, new Temp.DefaultMap());
          System.out.println(proc.frame.toString());

          LinkedList<Tree.Stm> stms = Canon.Canon.linearize(proc.body);
          proc.frame.procEntryExit1(stms);

          for (Tree.Stm s : (LinkedList<Tree.Stm>)stms.clone()) {
            java.io.PrintWriter writer = new PrintWriter(System.out);
            Tree.Print printVisitor = new Tree.Print(writer, s);
            writer.flush();
          }

          Canon.BasicBlocks basicBlocks = new Canon.BasicBlocks((LinkedList<Tree.Stm>)stms.clone());
          Canon.TraceSchedule ts = new Canon.TraceSchedule(basicBlocks);

          System.out.println("***********************************");
          System.out.println("***********************************");
          System.out.println("***********************************");

          LinkedList<Instr> instrs = new LinkedList<Instr>();
          for (LinkedList<LinkedList<Tree.Stm>> trace : ts.traces) {
            for (LinkedList<Tree.Stm> block : trace) {
              for (Tree.Stm s : block) {
                LinkedList<Instr> ins = new LinkedList<Instr>();
                s.accept(new Codegen((MipsFrame)proc.frame, (ListIterator<Instr>)ins.iterator()));
                instrs.addAll(instrs.size(), ins);
              }
            }
          }

          for (Instr instr : instrs) {
            System.out.println(instr.format(new MipsTempMap((MipsFrame)proc.frame)));
          }

        }
        // else if (f instanceof DataFrag) {
        //   System.out.println("PROGRAM_TAIL");
        // }
      }
    } catch(Exception e) {
      java.io.PrintWriter writer = new PrintWriter(System.out);
      e.printStackTrace(writer);
      writer.flush();
    }
  }
}
