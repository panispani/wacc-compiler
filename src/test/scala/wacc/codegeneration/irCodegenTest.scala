package wacc.codegeneration

import wacc.constructs._
import wacc.visitors.{ProgramVisitor, TestUtilities}

class irCodegenTest extends CodeGenTest {
  "Running a test" should "be possible" in {
    val parser = TestUtilities.setupParser("begin int x = 1 end")
    val program = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)
    val irCodegen = new irCodegenerator
    irCodegen.codegen(program.right.get)
  }
}
