package wacc.visitors

import wacc.{SymbolTable, TestUtilities, VariableReference}
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

    result.right.value(1) should be (
      AssignStatement(
        ArrayElement(VariableReference("x", ArrayType(Integer), -4), List(IntegerLiteral(0)), Integer),
        IntegerLiteral(10)
      )
    )

  }

  "Assigning to struct" should "build an AssignStatement" in {
    SymbolTable.structsTable += "car" -> Struct("car", Seq(VariableReference("a", Integer, 0), VariableReference("b", Character, 0)))
    val parser = TestUtilities.setupParser("struct car c = {1, 'a'} ; c = {2, 'b'}")
    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)

    result.right.value(1) should be (
      AssignStatement(
        VariableReference("c",StructType("car",List(("a", Integer), ("b", Character))),-4),
        StructLiteral(
          List(
            IntegerLiteral(2),
            CharLiteral("b")
          )
        )
      )
    )

  }

}


