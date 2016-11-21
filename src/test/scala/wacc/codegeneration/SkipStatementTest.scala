package wacc.codegeneration

import wacc.TransStatements._
import wacc.constructs.{SkipStatement}

class SkipStatementTest extends CodeGenTest {
  "A skip statement" should "translate to nothing" in {
    val availableRegisters = Seq(R4, R5, R6)
    val instructions = transStatement(SkipStatement(), availableRegisters)

    instructions.size should be (0)
  }
}
