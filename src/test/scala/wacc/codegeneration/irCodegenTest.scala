package wacc.codegeneration

import wacc.TransStatements._
import wacc.constructs._
import wacc.visitors.{ProgramVisitor, StatementVisitor, TestUtilities}

class irCodegenTest extends CodeGenTest {
  "Creating an integer" should "be possible" in {
    val parser = TestUtilities.setupParser("begin int x = 1 end")
    val program = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)
    val irCodegen = new IrCodegenerator
    println(irCodegen.codegen(program.right.get))
  }

  "Creating an character" should "be possible" in {
    val parser = TestUtilities.setupParser("begin char x = 'a' end")
    val program = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)
    val irCodegen = new IrCodegenerator
    println(irCodegen.codegen(program.right.get))
  }

  "Creating an boolean" should "be possible" in {
    val parser = TestUtilities.setupParser("begin bool x = true end")
    val program = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)
    val irCodegen = new IrCodegenerator
    println(irCodegen.codegen(program.right.get))
  }

  "Creating a lot of variables" should "reserve and release them together" in {
    val parser = TestUtilities.setupParser("begin int x = 1; char c = 'a' end")
    val program = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)
    val irCodegen = new IrCodegenerator
    println(irCodegen.codegen(program.right.get))
  }


    "Exiting" should "produce the expected instructions" in {
    val parser = TestUtilities.setupParser("exit 7")
    val program: Either[CompilationError, Statement] = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)
    val availableRegisters = Seq(R4, R5, R6)
    val instructions = transStatement(program.right.get, availableRegisters)
      println(instructions)
    instructions shouldBe Seq(MOV(R4, ImmOperand(7)), MOV(R0, RegisterOperand(R4)), BL(Label("exit")))
  }
}
