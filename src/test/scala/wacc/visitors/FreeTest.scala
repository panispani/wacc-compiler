package wacc.visitors

import wacc.constructs._

class FreeTest extends VisitorTest {

  "Visiting free" should "create a FreeStatement" in {
    val parser = TestUtilities.setupParser("pair(int, int) p = null ; read fst p")
    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)

    result.right.value should be (
      Seq(
        DeclareStatement(
          PairType(Integer, Integer),
          "p",
          PairLiteral()
        ),
        ReadStatement(
          PairElement(
            FirstSelector,
            VariableReferenceExpression(PairType(Integer, Integer)),
            Integer
          )
        )
      )
    )
  }

}
