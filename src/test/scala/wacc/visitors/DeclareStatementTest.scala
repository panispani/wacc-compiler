package wacc.visitors

import wacc.VariableReference
import wacc.constructs._

class DeclareStatementTest extends VisitorTest {

  "Declaration statement" should "be valid when lhs is pair and rhs is null" in {
    val parser = TestUtilities.setupParser("pair(int, int) p = null")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be
      DeclareStatement(
        PairType(Integer,Integer),
        VariableReference("p", PairType(Integer, Integer), 0),
        PairLiteral())
  }

  it should "be valid when lhs is array-type and rhs an empty array" in {
    val parser = TestUtilities.setupParser("int[] p = []")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be (
      DeclareStatement(
        ArrayType(Integer),
        VariableReference("p", ArrayType(Integer), 0),
        ArrayLiteral(List())))
  }

  it should "be invalid when lhs is pair and rhs empty array" in {
    val parser = TestUtilities.setupParser("pair(int, int) p = []")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.left.value shouldBe a[SemanticError]
  }

  "Integer declaration " should " be built correctly " in {
    val input = "begin int x = 1 end"
    val program = TestUtilities.buildProgram(input)
    val statements = Seq(
      DeclareStatement(
        Integer,
        VariableReference("x", Integer, 0),
        IntegerLiteral(1)
      )
    )

    program.right.value should be (Program(Seq(), statements))
  }

  "Pair declaration " should " be built correctly " in {
    val parser = TestUtilities.setupParser("pair(int, int) p = newpair(1,2)")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be (
      DeclareStatement(
        PairType(Integer, Integer),
        VariableReference("p", PairType(Integer, Integer), 0),
        PairConstructor(IntegerLiteral(1), IntegerLiteral(2))
      )
    )
  }

  "Array declaration" should "work with array literals on the RHS" in {
    val parser = TestUtilities.setupParser("int[] x = [1,2]")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be (
      DeclareStatement(
        ArrayType(Integer),
        VariableReference("x", ArrayType(Integer), 0),
        ArrayLiteral(Seq(IntegerLiteral(1), IntegerLiteral(2)))))
  }

  /*
  it should " throw a semantic error if the types don't match " {
  }
  */
}