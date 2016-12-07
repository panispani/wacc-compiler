package wacc.codegeneration

import wacc.TestUtilities
import wacc.arm._
import wacc.visitors.StatementVisitor

/**
  * Created by pp3414 on 22/11/16.
  */
class ScopeStatementTest extends CodeGenTest {
  "Visiting a new scope" should "allow re-declarations in new scopes" in {
    val parser = TestUtilities.setupParser("begin int a = 1; begin bool b = true; int a = 2 end end")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)
    val availableRegisters = Seq(R0, R1, R2, R3, R4, R5, R6, R7, R8)

    val instructions = program.right.get.transStatement(availableRegisters).instructions

    instructions.head shouldBe SUB(SP, SP, ImmOperand(4))
    instructions(1) shouldBe LDR(R0, Const(1))
    instructions(2) shouldBe STR(R0, RegisterAddress(FP, -4))

    // start of child scope
    instructions(3) shouldBe SUB(SP, SP, ImmOperand(5))

    instructions(4) shouldBe LDR(R0, Const(1))
    instructions(5) shouldBe STRB(R0, RegisterAddress(FP, -5))

    instructions(6) shouldBe LDR(R0, Const(2))
    instructions(7) shouldBe STR(R0, RegisterAddress(FP, -9))

    instructions(8) shouldBe ADD(SP, SP, ImmOperand(5))
    // end of child scope

    instructions(9) shouldBe ADD(SP, SP, ImmOperand(4))
  }
}
