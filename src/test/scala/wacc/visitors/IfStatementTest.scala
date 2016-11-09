package wacc.visitors

import org.scalatest.{EitherValues, FlatSpec, Matchers}
import wacc.constructs._

class IfStatementTest extends FlatSpec
  with Matchers
  with EitherValues {

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

  "If " should " throw a semantic error if the expression given is not a bool " in {
    program.left.value should be (SemanticError("Conditional statement expected expression of type bool, got PairType(PrimitiveType(int),PrimitiveType(int))"))
  }
}


