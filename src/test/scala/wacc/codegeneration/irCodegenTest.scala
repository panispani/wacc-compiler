package wacc.codegeneration

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


  //  "Exiting" should "produce the expected instructions" in {
//    val parser = TestUtilities.setupParser("exit 7")
//    val program: Either[CompilationError, Statement] = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)
//    val irCodegen = new irCodegenerator
//    val allRegisters = Seq(R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14)
//    irCodegen.transStatement(program.right.get, allRegisters) shouldBe Seq(LDRIMM(R4, 7), MOV(R0, R4), BL(Label("exit")))
//  }
}
