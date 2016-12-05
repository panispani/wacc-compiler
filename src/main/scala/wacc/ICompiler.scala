package wacc

import antlr.{WACCLexer, WACCParser}
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}
import wacc.arm.Registers
import wacc.codegeneration.{CodeSegment, TransProgram, TransStatements}
import wacc.visitors.{ProgramVisitor, StatementVisitor}

object ICompiler extends App {
  val input = new ANTLRInputStream(System.in)
  val lexer = new WACCLexer(input)
  val tokens = new CommonTokenStream(lexer)
  val parser = new WACCParser(tokens)

  parser.addErrorListener(new SyntaxErrorListener())

  // make it loop for statements and functions
  val tree = parser.statement()
  val statementAST = StatementVisitor.visit(tree)

  statementAST match {
    case Left(error) => error.raise()
    case Right(stmt) => CodeSegment().extend(TransStatements.transStatement(stmt, Registers.expressionRegs)).release()
  }

}
