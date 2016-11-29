package wacc.codegeneration

import org.scalatest.Ignore
import wacc.arm._
import wacc.visitors.StatementVisitor
import wacc.{SymbolTable, TestUtilities}

/**
  * Created by pp3414 on 22/11/16.
  */
@Ignore
class ScopeStatementTest extends CodeGenTest {
  "Visiting a new scope" should "allow re-declarations in new scopes" in {
    val parser = TestUtilities.setupParser("begin int a = 1; begin bool b = true; int a = 2 end end")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)
    val availableRegisters = Seq(R0, R1, R2, R3, R4, R5, R6, R7, R8)

    val instructions = TransStatements.transStatement(program.right.get, availableRegisters)

    instructions.head shouldBe SUB(SP, SP, ImmOperand(4))
    instructions(1) shouldBe MOV(R0, ImmOperand(1))
    // dont care about body instructions since they are up to transStatement
    instructions(2) shouldBe STR(R0, RegisterAddress(SP, 0))
    instructions(3) shouldBe SUB(SP, SP, ImmOperand(5))
    instructions(4) shouldBe MOV(R0, ImmOperand(1))
    instructions(5) shouldBe STRB(R0, RegisterAddress(SP, 0))
    instructions(6) shouldBe MOV(R0, ImmOperand(2))
    instructions(7) shouldBe STR(R0, RegisterAddress(SP, 1))
    instructions(8) shouldBe ADD(SP, SP, ImmOperand(5))
    instructions(9) shouldBe ADD(SP, SP, ImmOperand(4))
  }
}
