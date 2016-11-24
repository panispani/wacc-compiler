package wacc.codegeneration

import org.scalatest.Ignore
import wacc.{SymbolTable, TestUtilities}
import wacc.TransStatements._
import wacc.arm._
import wacc.visitors.StatementVisitor

class DeclareStatementTest extends CodeGenTest {

  it should "produce the expected instructions" in {
    val parser = TestUtilities.setupParser("int x = 42")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    val availableRegisters = Seq(R4, R5, R6)
    val instructions = transStatement(program.right.get, SymbolTable.globalTable, availableRegisters)

    instructions.last shouldBe STR(availableRegisters.head, RegisterAddress(FP, -4))
  }

  it should "produce the expected instructions with pair literal" in {
    val parser = TestUtilities.setupParser("pair(int, int) x = null")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    val availableRegisters = Seq(R4, R5, R6)
    val instructions = transStatement(program.right.get, SymbolTable.globalTable, availableRegisters)

    instructions.last shouldBe STR(availableRegisters.head, RegisterAddress(FP, -4))
  }

  it should "be able to handle two consecutive declarations" in {
    val parser = TestUtilities.setupParser("bool b = true")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    val parser2 = TestUtilities.setupParser("int x = 42")
    val program2 = TestUtilities.buildSubProgram(parser2.statement, StatementVisitor)

    val availableRegisters = Seq(R4, R5, R6)
    val instructions = transStatementSequence(
      Seq(program.right.get, program2.right.get),
      SymbolTable.globalTable,
      availableRegisters)

    instructions(1) shouldBe STRB(availableRegisters.head, RegisterAddress(FP, -1))
    instructions(3) shouldBe STR(availableRegisters.head, RegisterAddress(FP, -5))
  }

  it should "be able to handle array declarations" in {
    val parser = TestUtilities.setupParser("int[] a = [0, 1]")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    val availableRegisters = Seq(R4, R5, R6)
    val instructions = transStatement(program.right.get, SymbolTable.globalTable, availableRegisters)

    instructions.head shouldBe LDR(R0, Const(12))
    instructions(1) shouldBe BL(Label("malloc"))
    instructions(2) shouldBe MOV(availableRegisters.head, R0)
    instructions(3) shouldBe LDR(availableRegisters(1), Const(2))
    instructions(4) shouldBe STR(availableRegisters(1), RegisterAddress(availableRegisters.head, 0))
    //Don't care about instruction(5) because it's up to translateExpression
    instructions(6) shouldBe STR(availableRegisters(1), RegisterAddress(availableRegisters.head, 4))
    //Don't care about instruction(7) because it's up to translateExpression
    instructions(8) shouldBe STR(availableRegisters(1), RegisterAddress(availableRegisters.head, 8))
    instructions(9) shouldBe STR(availableRegisters.head, RegisterAddress(FP, -4))

  }

  it should "be able to handle declaring an int with assign value another variable" in {
    val parser = TestUtilities.setupParser("int a = 0")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    val parser2 = TestUtilities.setupParser("int b = a")
    val program2 = TestUtilities.buildSubProgram(parser2.statement, StatementVisitor)

    val availableRegisters = Seq(R4, R5, R6)
    val instructions = transStatementSequence(
      Seq(program.right.get, program2.right.get),
      SymbolTable.globalTable,
      availableRegisters)

    //Don't care about instructions(0) because it's up to translateExpression
    instructions(1) shouldBe STR(availableRegisters.head, RegisterAddress(FP, -4))
    instructions(2) shouldBe LDR(availableRegisters.head, RegisterAddress(FP, -4))
    instructions(3) shouldBe STR(availableRegisters.head, RegisterAddress(FP, -8))
  }

  it should "be able to handle declaring an array with assign value another variable" in {
    val parser = TestUtilities.setupParser("int[] a = [0]")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    val parser2 = TestUtilities.setupParser("int[] b = a")
    val program2 = TestUtilities.buildSubProgram(parser2.statement, StatementVisitor)

    val availableRegisters = Seq(R4, R5, R6)
    val instructions = transStatementSequence(
      Seq(program.right.get, program2.right.get),
      SymbolTable.globalTable,
      availableRegisters)

    instructions.head shouldBe LDR(R0, Const(8))
    instructions(1) shouldBe BL(Label("malloc"))
    instructions(2) shouldBe MOV(availableRegisters.head, R0)
    instructions(3) shouldBe LDR(availableRegisters(1), Const(1))
    instructions(4) shouldBe STR(availableRegisters(1), RegisterAddress(availableRegisters.head, 0))
    //Don't care about instruction(5) because it's up to translateExpression
    instructions(6) shouldBe STR(availableRegisters(1), RegisterAddress(availableRegisters.head, 4))
    instructions(7) shouldBe STR(availableRegisters.head, RegisterAddress(FP, -4))
    instructions(8) shouldBe LDR(availableRegisters.head, RegisterAddress(FP, -4))
    instructions(9) shouldBe STR(availableRegisters.head, RegisterAddress(FP, -8))
  }

  it should "be able to handle declaring pairs" in {
    val parser = TestUtilities.setupParser("pair(int, int) p = newpair(10, 3)")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    val availableRegisters = Seq(R4, R5, R6)
    val instructions = transStatement(program.right.get, SymbolTable.globalTable, availableRegisters)

    instructions.head shouldBe LDR(R0, Const(8))
    instructions(1) shouldBe BL(Label("malloc"))
    instructions(2) shouldBe MOV(availableRegisters.head, R0)
    //instructions(3) shouldBe MOV(availableRegisters(1), ImmOperand(10))
    instructions(4) shouldBe LDR(R0, Const(4))
    instructions(5) shouldBe BL(Label("malloc"))
    instructions(6) shouldBe STR(availableRegisters(1), RegisterAddress(R0, 0))
    instructions(7) shouldBe STR(R0, RegisterAddress(availableRegisters.head, 0))
    //expression
    instructions(9) shouldBe LDR(R0, Const(4))
    instructions(10) shouldBe BL(Label("malloc"))
    instructions(11) shouldBe STR(availableRegisters(1), RegisterAddress(R0, 0))
    instructions(12) shouldBe STR(R0, RegisterAddress(availableRegisters.head, 4))
    instructions(13) shouldBe STR(R4, RegisterAddress(FP, -4))

//    6   LDR r0, =8
//    7		BL malloc
//    8		MOV r4, r0
//    9		LDR r5, =10   expression
//    10		LDR r0, =4
//    11		BL malloc
//    12		STR r5, [r0]
//    13		STR r0, [r4]
//    14		LDR r5, =3   expression
//    15		LDR r0, =4
//    16		BL malloc
//    17		STR r5, [r0]
//    18		STR r0, [r4, #4]
//    19		STR r4, [sp]

  }
}
