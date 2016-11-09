package wacc.visitors

import antlr.{WACCLexer, WACCParser}
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}
import org.scalatest.{FlatSpec, Matchers}
import wacc.SyntaxErrorListener
import wacc.constructs.{Program, Skip, Statement}

class SkipTest extends FlatSpec with Matchers {


  "Skip " should " be built correctly " in {
    val input = "begin skip end"
    val program = TestUtilities.buildProgram(input)
    program should be (Program(Seq(), Seq(Skip())))
  }
}


