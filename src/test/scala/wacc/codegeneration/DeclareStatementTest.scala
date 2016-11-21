package wacc.codegeneration

import wacc.TransStatements._
import wacc.visitors.{StatementVisitor, TestUtilities}

class DeclareStatementTest extends  CodeGenTest {

  it should "produce the expected instructions" in {
    val parser = TestUtilities.setupParser("int x = 42")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    val availableRegisters = Seq(R4, R5, R6)
    val instructions = transStatement(program.right.get, availableRegisters)

    instructions.last shouldBe STR(availableRegisters.head, RegisterAddress(SP, 0))
  }

  it should "be able to handle two consecutive declarations" in {
    val parser = TestUtilities.setupParser("bool b = true")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    val parser2 = TestUtilities.setupParser("int x = 42")
    val program2 = TestUtilities.buildSubProgram(parser2.statement, StatementVisitor)

    val availableRegisters = Seq(R4, R5, R6)
    val instructions = transStatementSequence(Seq(program.right.get, program2.right.get), availableRegisters)

    instructions(1) shouldBe STRB(availableRegisters.head, RegisterAddress(SP, 0))
    instructions(3) shouldBe STR(availableRegisters.head, RegisterAddress(SP, 1))
  }

  it should "be able to handle array declarations" in {
    val parser = TestUtilities.setupParser("int[] a = [0, 1]")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    val availableRegisters = Seq(R4, R5, R6)
    val instructions = transStatement(program.right.get, availableRegisters)

    instructions.head shouldBe LDR(R0, Const(12))
    instructions(1) shouldBe BL(Label("malloc"))
    instructions(2) shouldBe MOV(availableRegisters.head, RegisterOperand(R0))
    instructions(3) shouldBe LDR(availableRegisters(1), Const(2))
    instructions(4) shouldBe STR(availableRegisters(1), RegisterAddress(availableRegisters.head, 0))
    //Don't care about instruction(5) because it's up to translateExpression
    instructions(6) shouldBe STR(availableRegisters(1), RegisterAddress(availableRegisters.head, 4))
    //Don't care about instruction(7) because it's up to translateExpression
    instructions(8) shouldBe STR(availableRegisters(1), RegisterAddress(availableRegisters.head, 8))
    instructions(9) shouldBe STR(availableRegisters.head, RegisterAddress(SP, 0))

  }
}
