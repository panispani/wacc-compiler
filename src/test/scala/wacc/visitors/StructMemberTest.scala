package wacc.visitors

import org.scalatest.Ignore
import wacc.constructs._
import wacc.{SymbolTable, TestUtilities, VariableReference}

@Ignore
class StructMemberTest extends VisitorTest {

  "Visiting a struct member" should "create struct construct with the correct symbol table" in {
    SymbolTable.structsTable += "car" -> Struct("car", Seq(VariableReference("a", Integer, 0), VariableReference("b", Character, 0)))
    val parser = TestUtilities.setupParser("begin struct car c = {1, 2}; c.x = 5 end ")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result
  }

}
