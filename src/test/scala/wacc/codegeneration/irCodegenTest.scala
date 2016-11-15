package wacc.codegeneration

import wacc.constructs._

class irCodegenTest extends CodeGenTest {
  "Running a test" should "be possible" in {
    val program = Program(Seq(), Seq(DeclareStatement(PrimitiveType("int"),"x",IntegerLiteral(1))))
    val ircodegen = new irCodegenerator
    ircodegen.codegen(program)
  }
}
