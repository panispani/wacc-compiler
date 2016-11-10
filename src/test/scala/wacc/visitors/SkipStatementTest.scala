package wacc.visitors

import wacc.constructs.Program

class SkipStatementTest extends VisitorTest {

  "Skip " should " be built correctly " in {
    val input = "begin skip end"
    val program = TestUtilities.buildProgram(input)
    program.right.value should be (Program(Seq(), Seq(SkipStatement())))
  }
}