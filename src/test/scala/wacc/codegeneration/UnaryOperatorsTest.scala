package wacc.codegeneration

import org.scalatest.Ignore
import wacc.TransStatements._
import wacc.arm._
import wacc.visitors.StatementVisitor
import wacc.{StaticCode, SymbolTable, TestUtilities}

class UnaryOperatorsTest extends CodeGenTest {

  "calling len on an array" should "produce the expected instructions" in {
    val parser = TestUtilities.setupParser("int[] a = [1]")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    val parser2 = TestUtilities.setupParser("println len a")
    val program2 = TestUtilities.buildSubProgram(parser2.statement, StatementVisitor)

    val registers = Seq(R4, R5, R6)
    val instructions = transStatementSequence(Seq(program.right.get, program2.right.get), SymbolTable.globalTable, registers)


    //instructions(0) up to instructions(7) are not up to unary operator
    instructions(8) shouldBe LDR(registers.head, RegisterAddress(FP, -4))
    instructions(9) shouldBe LDR(registers.head, RegisterAddress(registers.head, 0))
    instructions(10) shouldBe MOV(R0, registers.head)

//    LDR r4, [sp]
//    24		LDR r4, [r4]
//    25		MOV r0, r4
  }

}
