package wacc.visitors

import wacc.TestUtilities
import wacc.constructs.{Program, SkipStatement}

class SkipStatementTest extends VisitorTest {

  "Skip " should " be built correctly " in {
    val parser = TestUtilities.setupParser("skip")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)
    program.right.value should be (SkipStatement())
  }
}