package wacc.visitors

import org.scalatest.{EitherValues, FlatSpec, Matchers}
import wacc.constructs.{Program, Skip}

class SkipTest extends FlatSpec
  with Matchers
  with EitherValues {


  "Skip " should " be built correctly " in {
    val input = "begin skip end"
    val program = TestUtilities.buildProgram(input)
    program.right.value should be (Program(Seq(), Seq(Skip())))
  }
}


