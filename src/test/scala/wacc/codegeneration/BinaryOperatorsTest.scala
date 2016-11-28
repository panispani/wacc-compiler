package wacc.codegeneration

import wacc.arm._
import wacc.codegeneration.predefined.StaticCode
import wacc.visitors.StatementVisitor
import wacc.{SymbolTable, TestUtilities}

class BinaryOperatorsTest extends CodeGenTest {

  ignore should "produce the expected instructions" in {
    val parser = TestUtilities.setupParser("int x = 5")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    val parser2 = TestUtilities.setupParser("int y = 3")
    val program2 = TestUtilities.buildSubProgram(parser2.statement, StatementVisitor)

    val parser3 = TestUtilities.setupParser("int z = x / y")
    val program3 = TestUtilities.buildSubProgram(parser3.statement, StatementVisitor)

    val registers = Seq(R4, R5, R6)
    val instructions = TransStatements.transStatementSequence(
      Seq(program.right.get, program2.right.get, program3.right.get), SymbolTable.globalTable, registers)

    //instruction(0) is not up to division
    //instruction(1) is not up to division
    //instruction(2) is not up to division
    //instruction(3) is not up to division
    //instruction(4) is not up to division
    //instruction(5) is not up to division
    instructions(6) shouldBe MOV(R0, registers.head)
    instructions(7) shouldBe MOV(R1, registers(1))
    instructions(8) shouldBe BL(StaticCode.checkDivideByZeroLabel)
    instructions(9) shouldBe BL(StaticCode.divisionLabel)
    //instruction(10) is up to declaration
    //instruction(11) is up to declaration

  }

  "Dividing two integers" should "produce the expected instructions" in {
    val parser = TestUtilities.setupParser("int x = 5 / 2")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    val registers = Seq(R4, R5, R6)
    val instructions = TransStatements.transStatement(program.right.get, SymbolTable.globalTable, registers)

    //instruction(0) is not up to division
    //instruction(1) is not up to division
    instructions(2) shouldBe MOV(R0, registers.head)
    instructions(3) shouldBe MOV(R1, registers(1))
    instructions(4) shouldBe BL(StaticCode.checkDivideByZeroLabel)
    instructions(5) shouldBe BL(StaticCode.divisionLabel)
    instructions(6) shouldBe MOV(registers.head, R0)
    //instruction(7) is up to declaration
    //instruction(8) is up to declaration
  }

  "Module operator between two integers" should "produce the expected instructions" in {
    val parser = TestUtilities.setupParser("int x = 5 % 2")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    val registers = Seq(R4, R5, R6)
    val instructions = TransStatements.transStatement(program.right.get, SymbolTable.globalTable, registers)

    //instruction(0) is not up to module
    //instruction(1) is not up to module
    instructions(2) shouldBe MOV(R0, registers.head)
    instructions(3) shouldBe MOV(R1, registers(1))
    instructions(4) shouldBe BL(StaticCode.checkDivideByZeroLabel)
    instructions(5) shouldBe BL(StaticCode.moduleLabel)
    instructions(6) shouldBe MOV(registers.head, R1)
    //instruction(7) is up to declaration
    //instruction(8) is up to declaration
  }
}
