package experimental

import antlr.{WACCLexer, WACCParser}
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}

/* This is an experimental compiler */
object Compiler extends App {
  val input = new ANTLRInputStream(System.in)
  val lexer = new WACCLexer(input)
  val tokens = new CommonTokenStream(lexer)
  val parser = new WACCParser(tokens)

  parser.addErrorListener(new SyntaxErrorListener())

  val tree = parser.program()
  val program = ProgramVisitor.visit(tree)
  System.out.println(program)
}
