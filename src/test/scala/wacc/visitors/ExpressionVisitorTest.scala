package wacc.visitors

import org.scalatest.{EitherValues, FlatSpec, Matchers}
import wacc.constructs.CharLiteral

class ExpressionVisitorTest extends FlatSpec
  with Matchers
  with EitherValues {

  "Visiting a char literal" should "create a char literal with the correct value" in {
    val parser = TestUtilities.setupParser("'a'")
    val result = TestUtilities.buildSubProgram(parser.expression, ExpressionVisitor)

    result.right.value should be (CharLiteral('a'))
  }
}
