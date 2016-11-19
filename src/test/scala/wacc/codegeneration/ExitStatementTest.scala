package wacc.codegeneration

import wacc.TransStatements._
import wacc.constructs.{CompilationError, Statement}
import wacc.visitors.{StatementVisitor, TestUtilities}

class ExitStatementTest extends  CodeGenTest {

  "Exiting" should "produce the expected instructions" in {
    val parser = TestUtilities.setupParser("exit 7")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)
    val availableRegisters = Seq(R4, R5, R6)
    val instructions = transStatement(program.right.get, availableRegisters)
    instructions shouldBe Seq(MOV(R4, ImmOperand(7)), MOV(R0, RegisterOperand(R4)), BL(Label("exit")))
  }
}
