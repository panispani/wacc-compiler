package wacc.codegeneration

import org.scalatest.{FlatSpec, Matchers}
import wacc.TransStatements._
import wacc.constructs.{ExitStatement, IntegerLiteral}

class ExitStatementInMain extends FlatSpec with Matchers {

  "An exit statement in main" should "BL to C exit" in {
    val availableRegisters = Seq(R4, R5, R6)
    val instructions = transStatement(ExitStatement(IntegerLiteral(1)), availableRegisters)

    instructions.size should be (3)
    instructions(1) should be (MOV(R0, RegisterOperand(R4)))
    instructions.last should be (BL(Label("exit")))
  }
}
