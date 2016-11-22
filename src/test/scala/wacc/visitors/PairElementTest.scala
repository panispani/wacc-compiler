package wacc.visitors

import wacc.{TestUtilities, VariableReference}
import wacc.constructs._

class PairElementTest extends VisitorTest {

  "Creating a new pair" should "be valid if both elements are defined pairs and are given the value null" in {
    val parser = TestUtilities.setupParser("pair(pair, pair) p = newpair(null, null) ;\n  print p")
    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)

    result.right.value should be(
        List(
          DeclareStatement(
            PairType(PairType(AnyType, AnyType), PairType(AnyType, AnyType)),
            VariableReference("p", PairType(PairType(AnyType, AnyType),PairType(AnyType, AnyType)), 0),
            PairConstructor(PairLiteral(),PairLiteral())
          ),
          PrintStatement(
            VariableReference(
              "p",
              PairType(PairType(AnyType, AnyType),PairType(AnyType, AnyType)),
              0
            )
          )
        )
      )
  }
}
