package wacc.codegeneration

import wacc.TestUtilities
import wacc.TransStatements._
import wacc.visitors.StatementVisitor

/**
  * Created by panayiotis on 21/11/16.
  */
class LoopStatementTest extends CodeGenTest {

  it should "chack condition and provide alternative branches" in {
    val parser = TestUtilities.setupParser("while (1 == 1) do skip done")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)
    val availableRegisters = Seq(R0, R1, R2, R3, R4, R5, R6, R7, R8)

    val instructions = transStatement(program.right.get, availableRegisters)
    println(instructions)
    instructions.head shouldBe B(Label("L0"), ALWAYS())
    instructions(1) shouldBe DefineLabel(Label("L1"))
    // dont care about body instructions since they are up to transStatement
    instructions(2) shouldBe DefineLabel(Label("L0"))
    instructions(3) shouldBe MOV(R1,ImmOperand(1),ALWAYS())
    instructions(4) shouldBe MOV(R0,ImmOperand(1),ALWAYS())
    instructions(5) shouldBe CMP(R0,RegisterOperand(R1))
    instructions(6) shouldBe MOV(R0,ImmOperand(1),EQ())
    instructions(7) shouldBe MOV(R0,ImmOperand(0),NE())
    instructions(8) shouldBe CMP(R0,ImmOperand(1))
    instructions.last shouldBe B(Label("L1"),EQ())
  }

}
