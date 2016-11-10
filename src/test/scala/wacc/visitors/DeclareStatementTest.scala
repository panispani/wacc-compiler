package wacc.visitors

import wacc.constructs._

/**
  * Created by panayiotis on 10/11/16.
  */
class DeclareStatementTest extends VisitorTest {

  "Declaration statement" should "be valid when lhs is pair and rhs is null" in {
    val parser = TestUtilities.setupParser("pair(int, int) p = null")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be
      DeclareStatement(PairType(PrimitiveType("int"),PrimitiveType("int")),"p",PairLiteral())
  }

  it should "be valid when lhs is array-type and rhs an empty array" in {
    val parser = TestUtilities.setupParser("int[] p = []")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be (DeclareStatement(ArrayType(PrimitiveType("int")),"p",ArrayLiteral(List())))
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
        "x",
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
        "p",
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
        "x",
        ArrayLiteral(Seq(IntegerLiteral(1), IntegerLiteral(2)))))
  }

  /*
  it should " throw a semantic error if the types don't match " {
  }
  */
}