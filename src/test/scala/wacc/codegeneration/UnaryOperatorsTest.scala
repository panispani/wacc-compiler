package wacc.codegeneration

import wacc.TestUtilities
import wacc.arm._
import wacc.visitors.StatementVisitor

class UnaryOperatorsTest extends CodeGenTest {

  "calling len on an array" should "produce the expected instructions" in {
    val parser = TestUtilities.setupParser("int[] a = [1]")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    val parser2 = TestUtilities.setupParser("println len a")
    val program2 = TestUtilities.buildSubProgram(parser2.statement, StatementVisitor)

    val registers = Seq(R4, R5, R6)
    val instructions = TransStatements.transStatementSequence(Seq(program.right.get, program2.right.get), registers)

    //instructions(0) up to instructions(7) are not up to unary operator
    instructions(8) shouldBe LDR(registers.head, RegisterAddress(FP, -4))
    instructions(9) shouldBe LDR(registers.head, RegisterAddress(registers.head, 0))

    //    LDR r4, [sp]
    //    24		LDR r4, [r4]
    //    25		MOV r0, r4
  }

}
