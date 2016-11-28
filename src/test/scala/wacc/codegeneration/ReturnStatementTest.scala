package wacc.codegeneration

import wacc.arm._
import wacc.visitors.StatementVisitor
import wacc.{SymbolTable, TestUtilities}

class ReturnStatementTest extends  CodeGenTest {

  "Returning" should "produce the expected instructions" in {
    val parser = TestUtilities.setupParser("return 7")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)
    val availableRegisters = Seq(R4, R5, R6)
    val instructions = TransStatements.transStatement(program.right.get, SymbolTable.globalTable, availableRegisters)
    instructions.head shouldBe LDR(R4, Const(7))
    instructions(1) shouldBe MOV(R0, R4)
  }
}
