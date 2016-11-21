package wacc.codegeneration

import wacc.visitors.{ProgramVisitor, TestUtilities}

/**
  * Created by panayiotis on 18/11/16.
  */
class StatementSequenceTest extends  CodeGenTest {
  "Translating a sequence of statements" should "be possible" in {
    val parser = TestUtilities.setupParser("begin exit 10; exit 6 end")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    val availableRegisters = Seq(R4, R5)
    val instructions = transNext(result.right.get, availableRegisters)


    instructions.head should be (MOV(R4, ImmOperand(10)))
    instructions(1) should be (MOV(R0, RegisterOperand(R4)))
    instructions(2) should be (BL(Label("exit")))
    instructions(3) should be (MOV(R4, ImmOperand(6)))
    instructions(4) should be (MOV(R0, RegisterOperand(R4)))
    instructions(5) should be (BL(Label("exit")))
  }
}
