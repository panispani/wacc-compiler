package wacc.visitors

import org.scalatest.{EitherValues, FlatSpec, Matchers}
import wacc.constructs.{ExitStatement, IntegerLiteral, SemanticError}

class StatementVisitorTest extends FlatSpec
  with Matchers
  with EitherValues {

  "Visiting exit" should "create an exit statement" in {
    val parser = TestUtilities.setupParser("exit 10")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be (ExitStatement(IntegerLiteral(10)))
  }

  it should "be a semantic error when the exit code expression evaluates to a non-integer type" in {
    val parser = TestUtilities.setupParser("exit true")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.left.value should be (SemanticError("Exit statement code should evaluate to value of type int"))
  }

  // TODO: enable when integer expressions are implemented
  ignore should "create an exit statement with integer expression exit codes" in {
    val parser = TestUtilities.setupParser("exit 2 + 3")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

//    result.right.value should be (ExitStatement(Add(IndetegerLiteral(2), IntegerLiteral(3))))
  }

}
