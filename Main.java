import minijava.parser.*;
import minijava.lexer.*;
import minijava.node.*;
import java.io.*;

public class Main {
  public static void main(String[] arguments) {
    try {
      // Create a lexer instance.
      Lexer lexer = new Lexer(new PushbackReader(new InputStreamReader(System.in), 1024));
      Parser parser = new Parser(lexer);
      Start ast = parser.parse();
      ast.apply(new PrettyPrinter());
    } catch(Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
