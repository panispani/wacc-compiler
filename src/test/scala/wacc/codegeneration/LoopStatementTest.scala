package wacc.codegeneration

import wacc.TestUtilities
import wacc.arm._
import wacc.constructs.ForLoopStatement
import wacc.visitors.StatementVisitor

class LoopStatementTest extends CodeGenTest {


  ignore should "check condition and provide alternative branches" in {
    val parser = TestUtilities.setupParser("while (1 == 1) do skip done")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)
    val availableRegisters = Seq(R0, R1, R2, R3, R4, R5, R6, R7, R8)

    val instructions = TransStatements.transStatement(program.right.get, availableRegisters)

    println(instructions)

    instructions(3) shouldBe B(Label("L0"), ALWAYS)
    instructions(4) shouldBe DefineLabel(Label("L1"))

    instructions.slice(2, 4) should be (Seq(PUSH(Seq(FP)), MOV(FP, SP)))
    // dont care about body instructions since they are up to transStatement
    instructions(5) shouldBe SUB(SP, SP, ImmOperand(0))
    instructions(6) shouldBe ADD(SP, SP, ImmOperand(0))
    instructions(7) shouldBe DefineLabel(Label("L0"))
    instructions(8) shouldBe MOV(R1,ImmOperand(1),ALWAYS)
    instructions(8) shouldBe MOV(R0,ImmOperand(1),ALWAYS)
    instructions(9) shouldBe CMP(R0,R1)
    instructions(10) shouldBe MOV(R0,ImmOperand(1),EQ)
    instructions(11) shouldBe MOV(R0,ImmOperand(0),NE)
    instructions(12) shouldBe CMP(R0,ImmOperand(1))
    instructions.last shouldBe B(Label("L1"),EQ)
  }

  ignore should "declare variables each time it enters the loop" in {
    val parser = TestUtilities.setupParser("while (1 == 1) do int i = 21; int j = 2 done")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)
    val availableRegisters = Seq(R0, R1, R2, R3, R4, R5, R6, R7, R8)

    val instructions = TransStatements.transStatement(program.right.get, availableRegisters)
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

  "Do-while loop statement" should "not include branching on entry" in {
    val parser = TestUtilities.setupParser("do int i = 21 while (1 == 1)")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)
    val availableRegisters = Seq(R0, R1, R2, R3, R4, R5, R6, R7, R8)

    val instructions = TransStatements.transStatement(program.right.get, availableRegisters)

    instructions should not contain B(Label("L0"), ALWAYS)
  }

  "A for loop" should "execute init before branching" in {
    val parser = TestUtilities.setupParser("for int i = 0; i < 5; i = i + 1 do skip done")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)
    val availableRegisters = Seq(R0, R1, R2, R3, R4, R5, R6, R7, R8)

    val instructions = TransStatements.transStatement(program.right.get, availableRegisters)

    val labels = instructions.filter({
      case DefineLabel(_) => true
      case _ => false
    })

    val frameSize = 2
    val firstLabelIdx = instructions.indexOf(labels.head)

    val initStatement = instructions.slice(frameSize, firstLabelIdx - 1)

    val init = program.right.value.asInstanceOf[ForLoopStatement].init
    initStatement should be (init.transStatement(availableRegisters).instructions)

  }

  it should "execute step as the last statement of loop body" in {
    val parser = TestUtilities.setupParser("for int i = 0; i < 5; i = i + 1 do skip done")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)
    val availableRegisters = Seq(R0, R1, R2, R3, R4, R5, R6, R7, R8)

    val instructions = TransStatements.transStatement(program.right.get, availableRegisters)

    val labels = instructions.filter({
      case DefineLabel(_) => true
      case _ => false
    })

    val startIdx = instructions.indexOf(labels.head) + 1
    val endIdx = instructions.indexOf(labels.last)

    val loopBodyAndStep = instructions.slice(startIdx, endIdx)

    val step = program.right.value.asInstanceOf[ForLoopStatement].step
    loopBodyAndStep should be (step.transStatement(availableRegisters).instructions)

  }

}
