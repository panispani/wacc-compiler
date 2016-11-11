package wacc

import antlr.{WACCLexer, WACCParser}
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}
import wacc.visitors.ProgramVisitor

object Compiler extends App {
  val input = new ANTLRInputStream(System.in)
  val lexer = new WACCLexer(input)
  val tokens = new CommonTokenStream(lexer)
  val parser = new WACCParser(tokens)

  parser.addErrorListener(new SyntaxErrorListener())

  val tree = parser.program()
  val program = ProgramVisitor.visit(tree)

  program match {
    case Right(_) => System.exit(0)
    case Left(error) => error.raise()
  }
}
