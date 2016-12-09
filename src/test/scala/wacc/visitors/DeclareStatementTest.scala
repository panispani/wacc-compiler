package wacc.visitors

import wacc.{SymbolTable, TestUtilities, VariableReference}
import wacc.constructs._

class DeclareStatementTest extends VisitorTest {

  "Declaration statement" should "be valid when lhs is pair and rhs is null" in {
    val parser = TestUtilities.setupParser("pair(int, int) p = null")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be
      DeclareStatement(
        PairType(Integer,Integer),
        VariableReference("p", PairType(Integer, Integer), -4),
        PairLiteral())
  }

  it should "be valid when lhs is array-type and rhs an empty array" in {
    val parser = TestUtilities.setupParser("int[] p = []")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be (
      DeclareStatement(
        ArrayType(Integer),
        VariableReference("p", ArrayType(Integer), -4),
        ArrayLiteral(List())))
  }

  it should "be invalid when lhs is pair and rhs empty array" in {
    val parser = TestUtilities.setupParser("pair(int, int) p = []")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.left.value shouldBe a[SemanticError]
  }

  it should "struct type declaration should be valid" in {
    val parser = TestUtilities.setupParser("struct car c = {1, 'a'}")
    SymbolTable.structsTable += "car" -> StructType("car", Seq(VariableReference("a", Integer, 0), VariableReference("b", Character, 0)))
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.get shouldBe DeclareStatement(
      StructType(
        "car",
        Seq(VariableReference("a", Integer, 0),
          VariableReference("b", Character, 0))
      ),
      VariableReference(
        "c",
        StructType("car",
          Seq(
            VariableReference("a", Integer, 0),
            VariableReference("b", Character, 0)
          )
        ),
        -4),
      StructLiteral(List(IntegerLiteral(1), CharLiteral("a"))))
  }

  //TODO: remove ignore and fix test
  ignore should "be semantic error when lhs is a struct that has not been defined" in {
    val parser = TestUtilities.setupParser("struct car c = {1, 2}")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.left.value shouldBe a[SemanticError]
  }

  "Integer declaration " should " be built correctly " in {
    val parser = TestUtilities.setupParser("int x = 1")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    program.right.value should be (DeclareStatement(
      Integer,
      VariableReference("x", Integer, -4),
      IntegerLiteral(1)
    ))
  }

  "Pair declaration " should " be built correctly " in {
    val parser = TestUtilities.setupParser("pair(int, int) p = newpair(1,2)")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be (
      DeclareStatement(
        PairType(Integer, Integer),
        VariableReference("p", PairType(Integer, Integer), -4),
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
        VariableReference("x", ArrayType(Integer), -4),
        ArrayLiteral(Seq(IntegerLiteral(1), IntegerLiteral(2)))))
  }

  /*
  it should " throw a semantic error if the types don't match " {
  }
  */
}