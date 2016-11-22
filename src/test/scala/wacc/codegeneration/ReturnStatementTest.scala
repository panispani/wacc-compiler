package wacc.codegeneration

import wacc.{SymbolTable, TestUtilities}
import wacc.TransStatements._
import wacc.visitors.StatementVisitor

class ReturnStatementTest extends  CodeGenTest {

  "Returning" should "produce the expected instructions" in {
    val parser = TestUtilities.setupParser("return 7")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)
    val availableRegisters = Seq(R4, R5, R6)
    val instructions = transStatement(program.right.get, SymbolTable.globalTable, availableRegisters)
    instructions shouldBe Seq(MOV(R4, ImmOperand(7)), MOV(R0, R4))
  }
}
