package wacc.visitors

import antlr.{WACCLexer, WACCParser}
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}
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
}
