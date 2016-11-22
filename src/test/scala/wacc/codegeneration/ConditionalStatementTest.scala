package wacc.codegeneration

import wacc.{SymbolTable, TestUtilities}
import wacc.TransStatements._
import wacc.visitors.StatementVisitor

/**
  * Created by panayiotis on 21/11/16.
  */
class ConditionalStatementTest extends CodeGenTest {

  it should "chack condition and provide alternative branches" in {
    val parser = TestUtilities.setupParser("if (1 == 1) then exit 5 else skip fi")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)
    val availableRegisters = Seq(R0, R1, R2, R3, R4, R5, R6, R7, R8)

    val instructions = transStatement(program.right.get, SymbolTable.globalTable, availableRegisters)
    println(instructions)
    instructions.head shouldBe MOV(R1,ImmOperand(1),ALWAYS())
    instructions(1) shouldBe MOV(R0,ImmOperand(1),ALWAYS())
    instructions(2) shouldBe CMP(R0,R1)
    instructions(3) shouldBe MOV(R0,ImmOperand(1),EQ())
    instructions(4) shouldBe MOV(R0,ImmOperand(0),NE())
    instructions(5) shouldBe CMP(R0,ImmOperand(0))
    instructions(6) shouldBe B(Label("L0"),EQ())
    instructions(7) shouldBe B(Label("L1"),ALWAYS())
    instructions(8) shouldBe DefineLabel(Label("L0"))
    // dont care about branch instructions since they are up to transStatement
    instructions.last shouldBe DefineLabel(Label("L1"))
  }

}
