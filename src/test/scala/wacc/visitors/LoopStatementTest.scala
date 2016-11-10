package wacc.visitors

import wacc.constructs._

/**
  * Created by panayiotis on 10/11/16.
  */
class LoopStatementTest extends VisitorTest {
  "A loop statement" should "allow re-declarations in its new scope" in {
    val parser = TestUtilities.setupParser("int x = 1; while true do int x = 1 done")
    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)

    result.right.value should be(
      List(
        DeclareStatement(PrimitiveType("int"),"x",IntegerLiteral(1)),
        Loop(BoolLiteral(true),
          List(
            DeclareStatement(PrimitiveType("int"),"x",IntegerLiteral(1))
          )
        )
      )
    )
  }
}