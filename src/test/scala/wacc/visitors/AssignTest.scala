package wacc.visitors

import org.scalatest.{FlatSpec, Matchers}
import wacc.constructs.{Declare, Integer, IntegerLiteral, Program, Skip, Variable}

class AssignTest extends FlatSpec with Matchers {

  "Assign " should " be built correctly " in {
    val input = "begin int x = 1 end"
    val program = TestUtilities.buildProgram(input)
    val statements = Seq(
      Declare(
        Integer,
        "x",
        IntegerLiteral(1)
      )
    )

    program should be (Program(Seq(), statements))
  }
}
