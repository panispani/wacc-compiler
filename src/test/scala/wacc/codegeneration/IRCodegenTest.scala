package wacc.codegeneration

import org.scalatest.Ignore
import wacc.TestUtilities
import wacc.visitors.ProgramVisitor

// TODO: These tests don't make any assertions
@Ignore
class IRCodegenTest extends CodeGenTest {
  "Creating an integer" should "be possible" in {
    val parser = TestUtilities.setupParser("begin int x = 1 end")
    val program = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)
    println(TransProgram.transProgram(program.right.get))
  }

  "Creating an character" should "be possible" in {
    val parser = TestUtilities.setupParser("begin char x = 'a' end")
    val program = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)
    println(TransProgram.transProgram(program.right.get))
  }

  "Creating an boolean" should "be possible" in {
    val parser = TestUtilities.setupParser("begin bool x = true end")
    val program = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)
    println(TransProgram.transProgram(program.right.get))
  }

  "Creating a lot of variables" should "reserve and release them together" in {
    val parser = TestUtilities.setupParser("begin int x = 1; char c = 'a' end")
    val program = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)
    println(TransProgram.transProgram(program.right.get))
  }


}
