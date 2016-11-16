package wacc.visitors

import wacc.constructs._

import scala.collection.SeqView

class ReadStatementTest extends VisitorTest {

  "Read " should " throw a semantic error when the target type is not an int or char " in {
    val input =
      "begin " +
      "  pair(int, int) p = newpair(1,2); " +
      "  read p " +
      "end"

    val program = TestUtilities.buildProgram(input)
    val statements = Seq(
      DeclareStatement(
        PairType(Integer, Integer),
        "p",
        PairConstructor(IntegerLiteral(1), IntegerLiteral(2))
      ),
      ReadStatement(VariableReferenceExpression("p" ,PairType(Integer, Integer)))
    )

    program.left.value should (be (a[SemanticError]) or be (a[List[_]]))
  }
}


