package wacc.visitors

import wacc.{TestUtilities, VariableReference}
import wacc.constructs._

class ArrayLiteralTest extends VisitorTest {
  "Assigning an array literal" should "be possible" in {
    val parser = TestUtilities.setupParser("begin\n  int[] a = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0] \n end")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result.right.value should be
      Program(
        List(),
        List(
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
      )
  }
}
