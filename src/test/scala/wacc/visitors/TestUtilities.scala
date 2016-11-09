package wacc.visitors

import antlr.{WACCLexer, WACCParser, WACCParserBaseVisitor}
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream, ParserRuleContext}
import wacc.SyntaxErrorListener

object TestUtilities {

  def buildProgram(inputString: String) = {
    val input = new ANTLRInputStream(inputString)
    val lexer = new WACCLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new WACCParser(tokens)

    parser.addErrorListener(new SyntaxErrorListener())

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
}
