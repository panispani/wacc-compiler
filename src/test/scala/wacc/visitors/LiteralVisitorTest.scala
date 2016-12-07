package wacc.visitors

import wacc.{TestUtilities, VariableReference}
import wacc.constructs._

class LiteralVisitorTest extends VisitorTest {

  "Visiting a decimal literal" should "parse it in decimal" in {
    val parser = TestUtilities.setupParser("int x = 12")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be (
        DeclareStatement(
          Integer,
          VariableReference("x", Integer, -4),
          IntegerLiteral(12)
        )
    )
  }

  "Visiting an octal literal" should "parse it in decimal" in {
    val parser = TestUtilities.setupParser("int x = 011")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be (
      DeclareStatement(
        Integer,
        VariableReference("x", Integer, -4),
        IntegerLiteral(9)
      )
    )
  }

  "Visiting a hex literal" should "parse it in decimal" in {
    val parser = TestUtilities.setupParser("int x = 0x1FE")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be (
      DeclareStatement(
        Integer,
        VariableReference("x", Integer, -4),
        IntegerLiteral(510)
      )
    )
  }

  "Visiting a binary literal" should "parse it in decimal" in {
    val parser = TestUtilities.setupParser("int x = 0b1101")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be (
      DeclareStatement(
        Integer,
        VariableReference("x", Integer, -4),
        IntegerLiteral(13)
      )
    )
  }

}