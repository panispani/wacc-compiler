package wacc.visitors

import wacc.TestUtilities
import wacc.constructs.SemanticError

class IfStatementTest extends VisitorTest {
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

    program.left.value should (be (a[SemanticError]) or be (a[List[_]]))
  }
}


