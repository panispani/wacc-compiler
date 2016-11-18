package wacc.codegeneration

import org.scalatest.{FlatSpec, Matchers}
import wacc.TransStatements._
import wacc.TransPrograms._
import wacc.constructs.{ExitStatement, IntegerLiteral}
import wacc.visitors.{SequenceVisitor, StatementVisitor, TestUtilities}

/**
  * Created by panayiotis on 18/11/16.
  */
class StatementSequenceTest extends  CodeGenTest {
  "Translating a sequence of statements" should "be possible" in {
    val parser = TestUtilities.setupParser("exit 10; exit 6")
    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)

    val availableRegisters = Seq(R0, R1, R2, R3, R4, R5)
    val instructions = transNext(result.right.get, availableRegisters)

    instructions.size should be (6)
    instructions.head should be (MOV(R0, ImmOperand(10)))
    instructions(1) should be (MOV(R0, RegisterOperand(R0)))
    instructions(2) should be (BL(Label("exit")))
    instructions(3) should be (MOV(R0, ImmOperand(6)))
    instructions(4) should be (MOV(R0, RegisterOperand(R0)))
    instructions.last should be (BL(Label("exit")))
  }
}
