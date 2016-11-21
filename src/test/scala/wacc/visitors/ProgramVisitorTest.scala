package wacc.visitors

import wacc.TestUtilities
import wacc.constructs.SemanticError

import scala.collection.SeqView

class ProgramVisitorTest extends VisitorTest {

  "Visiting program" should "be a semantic error when return statements are present in main scope" in {
    val parser = TestUtilities.setupParser("begin int x = 1 ; return x end")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result.left.value should (be (a[SemanticError]) or be (a[List[_]]))
  }

}