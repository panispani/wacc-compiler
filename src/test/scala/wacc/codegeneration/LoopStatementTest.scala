package wacc.codegeneration

import wacc.{SymbolTable, TestUtilities}
import wacc.TransStatements._
import wacc.arm._
import wacc.visitors.StatementVisitor

class LoopStatementTest extends CodeGenTest {

  it should "check condition and provide alternative branches" in {
    val parser = TestUtilities.setupParser("while (1 == 1) do skip done")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)
    val availableRegisters = Seq(R0, R1, R2, R3, R4, R5, R6, R7, R8)

    val instructions = transStatement(program.right.get, SymbolTable.globalTable, availableRegisters)
    instructions.head shouldBe B(Label("L0"), ALWAYS)
    instructions(1) shouldBe DefineLabel(Label("L1"))
    // dont care about body instructions since they are up to transStatement
    instructions(2) shouldBe SUB(SP, SP, ImmOperand(0))
    instructions(3) shouldBe ADD(SP, SP, ImmOperand(0))
    instructions(4) shouldBe DefineLabel(Label("L0"))
    instructions(5) shouldBe MOV(R1,ImmOperand(1),ALWAYS))
    instructions(6) shouldBe MOV(R0,ImmOperand(1),ALWAYS))
    instructions(7) shouldBe CMP(R0,R1)
    instructions(8) shouldBe MOV(R0,ImmOperand(1),EQ))
    instructions(9) shouldBe MOV(R0,ImmOperand(0),NE))
    instructions(10) shouldBe CMP(R0,ImmOperand(1))
    instructions.last shouldBe B(Label("L1"),EQ))
  }


  it should "declare variables each time it enters the loop" in {
    val parser = TestUtilities.setupParser("while (1 == 1) do int i = 21; int j = 2 done")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)
    val availableRegisters = Seq(R0, R1, R2, R3, R4, R5, R6, R7, R8)

    val instructions = transStatement(program.right.get, SymbolTable.globalTable, availableRegisters)
    println(instructions)
    instructions.head shouldBe B(Label("L0"),ALWAYS)
    instructions(1) shouldBe DefineLabel(Label("L1"))
    instructions(2) shouldBe SUB(SP,SP,ImmOperand(8))
    instructions(3) shouldBe MOV(R0,ImmOperand(21), ALWAYS)
    instructions(4) shouldBe STR(R0,RegisterAddress(SP,0))
    instructions(5) shouldBe MOV(R0,ImmOperand(2), ALWAYS)
    instructions(6) shouldBe STR(R0,RegisterAddress(SP,4))
    instructions(7) shouldBe ADD(SP,SP,ImmOperand(8))
    instructions(8) shouldBe DefineLabel(Label("L0"))
    instructions(9) shouldBe MOV(R1,ImmOperand(1), ALWAYS)
    instructions(10) shouldBe MOV(R0,ImmOperand(1), ALWAYS)
    instructions(11) shouldBe CMP(R0,R1)
    instructions(12) shouldBe MOV(R0,ImmOperand(1), EQ)
    instructions(13) shouldBe MOV(R0,ImmOperand(0), NE)
    instructions(14) shouldBe CMP(R0,ImmOperand(1))
    instructions(15) shouldBe B(Label("L1"),EQ)

  }
}
