package wacc.codegeneration

import wacc.TestUtilities
import wacc.visitors.{ProgramVisitor}

class irCodegenTest extends CodeGenTest {
  "Creating an integer" should "be possible" in {
    val parser = TestUtilities.setupParser("begin int x = 1 end")
    val program = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)
    val irCodegen = new IntermediateReprCodeGenerator
    println(irCodegen.generateCode(program.right.get))
  }

  "Creating an character" should "be possible" in {
    val parser = TestUtilities.setupParser("begin char x = 'a' end")
    val program = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)
    val irCodegen = new IntermediateReprCodeGenerator
    println(irCodegen.generateCode(program.right.get))
  }

  "Creating an boolean" should "be possible" in {
    val parser = TestUtilities.setupParser("begin bool x = true end")
    val program = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)
    val irCodegen = new IntermediateReprCodeGenerator
    println(irCodegen.generateCode(program.right.get))
  }

  "Creating a lot of variables" should "reserve and release them together" in {
    val parser = TestUtilities.setupParser("begin int x = 1; char c = 'a' end")
    val program = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)
    val irCodegen = new IntermediateReprCodeGenerator
    println(irCodegen.generateCode(program.right.get))
  }


}
