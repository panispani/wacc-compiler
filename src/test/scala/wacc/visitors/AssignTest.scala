package wacc.visitors

import wacc.constructs.{ArrayElement, ArrayLiteral, ArrayType, AssignStatement, DeclareStatement, IntegerLiteral, PairElement, SemanticError}

class AssignTest extends VisitorTest {

  "Assigning to a function" should "be a semantic error" in {
    val parser = TestUtilities.setupParser("" +
      "begin " +
      "int f() is return 3 end " +
      "f = 2 end")

    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result.left.value shouldBe a[SemanticError]
  }

  "Assigning to an array element" should "build an AssignStatement" in {
    val parser = TestUtilities.setupParser("int [] x = [1, 2, 3] ; x [0] = 10")
    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)

    /* TODO: Can't write this test case until ArrayElement is fixed
    result.right.value should be (
      Seq(
        DeclareStatement(
          ArrayType(Integer),
          "x",
          ArrayLiteral(Seq(IntegerLiteral(1), IntegerLiteral(2), IntegerLiteral(3)))
        ),
        AssignStatement(
          ArrayElement(

          )
        )
      )
    )
    */
  }

}
