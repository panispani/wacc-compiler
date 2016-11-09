package wacc.visitors

import org.scalatest.{EitherValues, FlatSpec, Matchers}
import wacc.constructs._

class IfStatementTest extends FlatSpec
  with Matchers
  with EitherValues {


  "If " should " throw a semantic error if the expression given is not a bool " in {
    val input =
      "begin " +
      "  pair(int, int) p = newpair(1,2); " +
      "  if (p) then " +
      "    skip " +
      "  else " +
      "    skip " +
      "  fi " +
      "end"

    val program = TestUtilities.buildProgram(input)
    val statements = Seq(
      DeclareStatement(
        PairType(Integer, Integer),
        "p",
        PairConstructor(IntegerLiteral(1), IntegerLiteral(2))
      ),
      ConditionalStatement(
        VariableReferenceExpression(PairType(Integer, Integer)),
        Seq(SkipStatement()),
        Seq(SkipStatement())
      )
    )

    program.left.value should be (SemanticError("Conditional statement expected expression of type bool, got PairType(PrimitiveType(int),PrimitiveType(int))"))
  }
}


