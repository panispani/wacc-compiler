package wacc.visitors

import wacc.constructs._
/**
  * Created by panayiotis on 11/11/16.
  */
class PairElementTest extends VisitorTest {

  "Creating a new pair" should "be valid if both elements are defined pairs and are given the value null" in {
    val parser = TestUtilities.setupParser("begin\n  pair(pair, pair) p = newpair(null, null) ;\n  print p\n end")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result.right.value should be(
      Program(
        List(),
        List(
          DeclareStatement(
            PairType(ErasedPair(),ErasedPair()),
            "p",
            PairConstructor(PairLiteral(),PairLiteral())
          ),
          PrintStatement(
            VariableReferenceExpression(
              "p",
              PairType(ErasedPair(),ErasedPair())
            )
          )
        )
      )
    )
  }
}
