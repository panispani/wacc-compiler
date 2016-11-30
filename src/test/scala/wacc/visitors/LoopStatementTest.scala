package wacc.visitors

import wacc.{TestUtilities, VariableReference}
import wacc.constructs._

/**
  * Created by panayiotis on 10/11/16.
  */
class LoopStatementTest extends VisitorTest {
  "A loop statement" should "allow re-declarations in its new scope" in {
    val parser = TestUtilities.setupParser("int x = 1; while true do int x = 1 done")
    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)

    result.right.value(1) should be (a[LoopStatement])
    val loop = result.right.value(1).asInstanceOf[LoopStatement]

    loop.symbolTable.lookup("x") shouldBe defined
  }

  "A do-while statement" should "be possible" in {
    val parser = TestUtilities.setupParser("int x = 1; do int x = 1 while true")
    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)

    result.right.value(1) should be (a[LoopStatement])
    val loop = result.right.value(1).asInstanceOf[LoopStatement]

    loop.doWhile shouldBe true
  }
}