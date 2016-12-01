package wacc.visitors

import wacc.constructs._
import wacc.{TestUtilities, VariableReference}

class StructLiteralTest extends VisitorTest {

  "Assigning a struct literal" should "be possible" in {
    val parser = TestUtilities.setupParser("{1, 2}")
    val result = TestUtilities.buildSubProgram(parser.structLiteral, StructLiteralVisitor)

    result.right.value should be (
      StructLiteral(
        List(
          IntegerLiteral(1),
          IntegerLiteral(2)
        )
      )
    )
  }

}
