package wacc.visitors

import wacc.{TestUtilities, VariableReference}
import wacc.constructs._
import wacc.constructs.Integer

import scala.collection.SeqView

class AssignTest extends VisitorTest {

  "Assigning to a function" should "be a semantic error" in {
    val parser = TestUtilities.setupParser("" +
      "begin " +
      "int f() is return 3 end " +
      "f = 2 end")

    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result.left.value should (be (a[SemanticError]) or be (a[List[_]]))
  }

  "Assigning to a string element" should "be possible with a character" in {
    val parser = TestUtilities.setupParser("begin\n  string s = \"hello world!\" ;\n s[0] = 'H'\nend")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    assert(result.isRight)
  }

  "Assigning to a character" should "be possible from a string element" in {
    val parser = TestUtilities.setupParser("begin\n  string s = \"hello world!\" ;\n char c = s[1]\nend")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    assert(result.isRight)
  }

  "Assigning to an array element" should "build an AssignStatement" in {
    val parser = TestUtilities.setupParser("int [] x = [1, 2, 3] ; x [0] = 10")
    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)

    result.right.value should be (
      List(
        DeclareStatement(
          ArrayType(Integer),
          VariableReference("x", ArrayType(Integer), -4),
          ArrayLiteral(
            List(
              IntegerLiteral(1),
              IntegerLiteral(2),
              IntegerLiteral(3)
            )
          )
        ),
        AssignStatement(
          ArrayElement(VariableReference("x", ArrayType(Integer), -4), List(IntegerLiteral(0)), Integer),
          IntegerLiteral(10)
        )
      )
    )

  }

}
