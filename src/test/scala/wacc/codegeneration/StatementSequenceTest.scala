package wacc.codegeneration

import wacc.TestUtilities
import wacc.visitors.ProgramVisitor
import wacc.arm._


class StatementSequenceTest extends CodeGenTest {

  "Translating a sequence of statements" should "be possible" in {
    val parser = TestUtilities.setupParser("begin exit 10; exit 6 end")
    val program = TestUtilities.buildSubProgram(parser.program, ProgramVisitor).right.get
    val instructions = TestUtilities.translateWithoutSections(program.main)

    instructions.head should be (PUSH(Seq(FP)))
    instructions(1) should be (MOV(FP, SP))
    instructions(2) should be (SUB(SP, SP, ImmOperand(0)))
    instructions(3) should be (LDR(R4, Const(10)))
    instructions(4) should be (MOV(R0, R4))
    instructions(5) should be (BL(Label("exit")))
    instructions(6) should be (LDR(R4, Const(6)))
    instructions(7) should be (MOV(R0, R4))
    instructions(8) should be (BL(Label("exit")))
  }
}
