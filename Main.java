import minijava.parser.*;
import minijava.lexer.*;
import minijava.node.*;
import visitor.*;
import Mips.*;
import java.io.*;
import java.util.*;

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

      // for (Iterator<visitor.Frag> frags = translate.getResults(); frags.hasNext(); ) {
      //   //get next fragment
      //   Frag f = frags.next();

      //   //if the fragment is a ProcFrag i.e one which contains a procedure
      //   //then I get the map of temps associated with it and print it out.

      //   if (f instanceof ProcFrag) {
      //     Temp.TempMap tempmap = new Temp.CombineMap(((ProcFrag)f).frame, new Temp.DefaultMap());

      //     // System.out.println("PROCEDURE :" + ((ProcFrag)f).frame.name);
      //     System.out.println(((ProcFrag)f).frame.toString());
      //   } else if (f instanceof DataFrag) {
      //     System.out.println("PROGRAM_TAIL");
      //   }
      // }
      InterpreterVisitor interpreter = new InterpreterVisitor(translate.getResults());
      interpreter.start();
    } catch(Exception e) {
      System.out.println(e);
    }
  }
}
