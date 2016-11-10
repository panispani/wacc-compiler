package wacc.visitors

import wacc.constructs._

class DeclareTest extends VisitorTest {

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
