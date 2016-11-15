package wacc.codegeneration

import wacc.constructs._
import wacc.codegeneration.irCodegenerator

class irCodegenTest extends CodeGenTest {
  "Running a test" should "be possible" in {
    val program = Program(Seq(), Seq())
    val ircodegen = new irCodegenerator
    ircodegen.codegen(program)
  }
}
