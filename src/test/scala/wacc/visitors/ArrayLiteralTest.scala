package wacc.visitors

import wacc.{TestUtilities, VariableReference}
import wacc.constructs._

class ArrayLiteralTest extends VisitorTest {
  "Assigning an array literal" should "be possible" in {
    val parser = TestUtilities.setupParser("int[] a = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be (
          DeclareStatement(
            ArrayType(Integer),
            VariableReference("a", ArrayType(Integer), 0),
            ArrayLiteral(
              List(
                IntegerLiteral(0),
                IntegerLiteral(0),
                IntegerLiteral(0),
                IntegerLiteral(0),
                IntegerLiteral(0),
                IntegerLiteral(0),
                IntegerLiteral(0),
                IntegerLiteral(0),
                IntegerLiteral(0),
                IntegerLiteral(0)
              )
            )
          )
        )
  }
}
