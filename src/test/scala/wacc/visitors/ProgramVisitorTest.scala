package wacc.visitors

import wacc.constructs.SemanticError

class ProgramVisitorTest extends VisitorTest {

  "Visiting program" should "be a semantic error when return statements are present in main scope" in {
    val parser = TestUtilities.setupParser("begin int x = 1 ; return x end")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result.left.value shouldBe a[SemanticError]
  }
}