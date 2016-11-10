package wacc.visitors

import wacc.constructs.SemanticError

class AssignTest extends VisitorTest {

  "Assigning from a function" should "be a semantic error" in {
    val parser = TestUtilities.setupParser("" +
      "begin " +
      "int f() is return 3 end " +
      "f = 2 end")

    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result.left.value shouldBe a[SemanticError]
  }
}
