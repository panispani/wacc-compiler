package wacc.visitors

import org.scalatest.{EitherValues, FlatSpec, Matchers}
import wacc.constructs.{ExitStatement, IntegerLiteral}

class StatementVisitorTest extends FlatSpec
  with Matchers
  with EitherValues {

  "Visiting exit" should "create an exit statement" in {
    val parser = TestUtilities.setupParser("exit 10")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be (ExitStatement(IntegerLiteral(10)))
  }

}
