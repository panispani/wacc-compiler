package wacc

import antlr.{WACCLexer, WACCParser, WACCParserBaseVisitor}
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream, ParserRuleContext}
import org.scalatest._
import wacc.constructs.{Program, Statement}
import wacc.visitors.ProgramVisitor

object TestUtilities {

  def buildProgram(inputString: String) = {
    val parser = setupParser(inputString)
    val tree = parser.program()
    val program = ProgramVisitor.visit(tree)

    program
  }

  def setupParser(code: String): WACCParser = {
    val input = new ANTLRInputStream(code)
    val lexer = new WACCLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new WACCParser(tokens)

    parser.addErrorListener(new SyntaxErrorListener())

    parser
  }

  def buildSubProgram[T](rule: () => ParserRuleContext, visitor: WACCParserBaseVisitor[T]) = {
    val tree = rule()
    val program = visitor.visit(tree)

    program
  }

  def functionlessProgram(statements: Seq[Statement]): Program = {
    Program(Seq(), statements)
  }

  trait SymbolTableState extends BeforeAndAfterEach { this: Suite =>

    override def afterEach(): Unit = {
      try super.afterEach()
      finally SymbolTable.clearAll()
    }
  }
}
