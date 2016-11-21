package wacc

import antlr.{WACCLexer, WACCParser}
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}
import wacc.visitors.ProgramVisitor
import wacc.TransProgram._

object Compiler extends App {
  val input = new ANTLRInputStream(System.in)
  val lexer = new WACCLexer(input)
  val tokens = new CommonTokenStream(lexer)
  val parser = new WACCParser(tokens)

  parser.addErrorListener(new SyntaxErrorListener())

  val tree = parser.program()
  val programAST = ProgramVisitor.visit(tree)

  programAST match {
    case Left(errors :+ last) => errors foreach(_.raise()) ; last.raiseAndExit()
    case Right(program) => transProgram(program).release()
  }

}
