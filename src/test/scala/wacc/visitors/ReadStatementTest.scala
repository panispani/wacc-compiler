package wacc.visitors

import org.scalatest.{EitherValues, FlatSpec, Matchers}
import wacc.constructs._
import wacc.visitors

class ReadStatementTest extends FlatSpec
  with Matchers
  with EitherValues {


  "Read " should " throw a semantic error when the target type is not an int or char " in {
    val input =
      "begin " +
      "  pair(int, int) p = newpair(1,2); " +
      "  read p " +
      "end"

    val program = TestUtilities.buildProgram(input)
    val statements = Seq(
      Declare(
        PairType(Integer, Integer),
        "p",
        PairConstructor(IntegerLiteral(1), IntegerLiteral(2))
      ),
      ReadStatement(VariableReferenceExpression(PairType(Integer, Integer)))
    )


    program.left.value should be (SemanticError("Read statement target must be of type int or char"))
  }
}


