package wacc.codegeneration

import wacc.SymbolTable
import wacc.TransStatements._
import wacc.arm.{R4, R5, R6}
import wacc.constructs.SkipStatement

class SkipStatementTest extends CodeGenTest {
  "A skip statement" should "translate to nothing" in {
    val availableRegisters = Seq(R4, R5, R6)

    // TODO: Symbol table should be mocked
    val instructions = transStatement(SkipStatement(), SymbolTable.globalTable, availableRegisters)

    instructions.size should be (0)
  }
}
