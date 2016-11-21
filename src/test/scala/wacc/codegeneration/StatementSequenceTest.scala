package wacc.codegeneration

import wacc.TestUtilities
import wacc.visitors.ProgramVisitor
import wacc.TransProgram._

/**
  * Created by panayiotis on 18/11/16.
  */
class StatementSequenceTest extends  CodeGenTest {

  "Translating a sequence of statements" should "be possible" in {
    val parser = TestUtilities.setupParser("begin exit 10; exit 6 end")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)
    val instructions = transProgram(result.right.get)

    instructions.head should be (SUB(SP, SP, ImmOperand(0)))
    instructions(1) should be (MOV(R4, ImmOperand(10)))
    instructions(2) should be (MOV(R0, RegisterOperand(R4)))
    instructions(3) should be (BL(Label("exit")))
    instructions(4) should be (MOV(R4, ImmOperand(6)))
    instructions(5) should be (MOV(R0, RegisterOperand(R4)))
    instructions(6) should be (BL(Label("exit")))
  }
}
