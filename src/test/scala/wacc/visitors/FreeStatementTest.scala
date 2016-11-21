package wacc.visitors

import wacc.VariableReference
import wacc.constructs._

class FreeStatementTest extends VisitorTest {

  "Visiting free" should "create a FreeStatement" in {
    val parser = TestUtilities.setupParser("pair(int, int) p = null ; read fst p")
    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)

    result.right.value should be (
      Seq(
        DeclareStatement(
          PairType(Integer, Integer),
          VariableReference("p", PairType(Integer, Integer), 0),
          PairLiteral()
        ),
        ReadStatement(
          PairElement(
            FirstSelector,
            VariableReference("p", PairType(Integer, Integer), 0),
            Integer
          )
        )
      )
    )
  }

}
