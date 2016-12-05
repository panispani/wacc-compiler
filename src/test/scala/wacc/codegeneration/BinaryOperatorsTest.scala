package wacc.codegeneration

import wacc.arm._
import wacc.codegeneration.predefined.StaticCode
import wacc.codegeneration.predefined.std.StandardLibrary
import wacc.visitors.StatementVisitor
import wacc.{SymbolTable, TestUtilities}

class BinaryOperatorsTest extends CodeGenTest {

  "Dividing two integers" should "produce the expected instructions" in {
    val parser = TestUtilities.setupParser("int x = 5 / 2")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    val registers = Seq(R4, R5, R6)
    val instructions = TransStatements.transStatement(program.right.get, registers)

    //instruction(0) is not up to division
    //instruction(1) is not up to division
    instructions(2) shouldBe MOV(R0, registers.head)
    instructions(3) shouldBe MOV(R1, registers(1))
    instructions(4) shouldBe BL(StaticCode.getStaticFunction(StandardLibrary.div))
    instructions(5) shouldBe MOV(registers.head, R0)
    //instruction(6) is up to declaration
    //instruction(7) is up to declaration
  }

  "Module operator between two integers" should "produce the expected instructions" in {
    val parser = TestUtilities.setupParser("int x = 5 % 2")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    val registers = Seq(R4, R5, R6)
    val instructions = TransStatements.transStatement(program.right.get, registers)

    //instruction(0) is not up to module
    //instruction(1) is not up to module
    instructions(2) shouldBe MOV(R0, registers.head)
    instructions(3) shouldBe MOV(R1, registers(1))
    instructions(4) shouldBe BL(StaticCode.getStaticFunction(StandardLibrary.mod))
    instructions(5) shouldBe MOV(registers.head, R1)
    //instruction(6) is up to declaration
    //instruction(7) is up to declaration
  }
}
