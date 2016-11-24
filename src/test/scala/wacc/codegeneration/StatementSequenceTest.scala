package wacc.codegeneration

import wacc.TestUtilities
import wacc.visitors.ProgramVisitor
import wacc.arm._


class StatementSequenceTest extends CodeGenTest {

  "Translating a sequence of statements" should "be possible" in {
    val parser = TestUtilities.setupParser("begin exit 10; exit 6 end")
    val program = TestUtilities.buildSubProgram(parser.program, ProgramVisitor).right.get
    val instructions = TestUtilities.translateWithoutSections(program.main)
println(instructions)
    instructions.head should be (PUSH(Seq(FP)))
    instructions(1) should be (MOV(FP, SP))
    instructions(2) should be (MOV(R4, ImmOperand(10)))
    instructions(3) should be (MOV(R0, R4))
    instructions(4) should be (BL(Label("exit")))
    instructions(5) should be (MOV(R4, ImmOperand(6)))
    instructions(6) should be (MOV(R0, R4))
    instructions(7) should be (BL(Label("exit")))
    instructions(8) should be (ADD(SP, SP, ImmOperand(0)))
  }
}
