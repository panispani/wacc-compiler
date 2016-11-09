package wacc.visitors

import org.scalatest.{EitherValues, FlatSpec, Matchers}
import wacc.VariableReference
import wacc.constructs._

class ExpressionVisitorTest extends FlatSpec
  with Matchers
  with EitherValues {

  "Visiting a char literal" should "create a CharLiteral with the correct value" in {
    val parser = TestUtilities.setupParser("'a'")
    val result = TestUtilities.buildSubProgram(parser.expression, ExpressionVisitor)

    result.right.value should be (CharLiteral('a'))
  }

  "Visiting a string literal" should "create a StringLiteral with correct value" in {
    val parser = TestUtilities.setupParser("\"parseMe\"")
    val result = TestUtilities.buildSubProgram(parser.expression, ExpressionVisitor)

      result.right.value should be (StringLiteral("\"parseMe\""))
  }

  "Visiting an int literal" should "create an IntegerLiteral" in {
    val parser = TestUtilities.setupParser("123")
    val result = TestUtilities.buildSubProgram(parser.expression, ExpressionVisitor)

    result.right.value should be (IntegerLiteral(123))
  }

  "Visiting an bool literal" should "create an BoolLiteral" in {
    val parser = TestUtilities.setupParser("true")
    val result = TestUtilities.buildSubProgram(parser.expression, ExpressionVisitor)

    result.right.value should be (BoolLiteral(true))
  }

  "Visiting a unary operator" should "create UnaryOperatorExp" in {
    val parser = TestUtilities.setupParser("--5")
    val result = TestUtilities.buildSubProgram(parser.expression, ExpressionVisitor)

    result.right.value should be (UnaryOperatorExpr(UnaryOperator("-"), IntegerLiteral(-5)))
  }


  "Visiting a valid binary expression(+)" should "create a Binary Expression with (+)" in {
    val parser = TestUtilities.setupParser("2+-5")
    val result = TestUtilities.buildSubProgram(parser.expression, ExpressionVisitor)

    result.right.value should be (BinaryOperatorExpr(IntegerLiteral(2),BinaryOperator("+"), IntegerLiteral(-5)))
  }

}
