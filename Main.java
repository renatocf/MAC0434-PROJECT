import minijava.parser.*;
import minijava.lexer.*;
import minijava.node.*;
import visitor.*;
import java.io.*;

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

      System.out.println("Ok!!!");

    } catch(Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
  }
}
