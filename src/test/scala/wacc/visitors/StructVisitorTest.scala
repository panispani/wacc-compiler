package wacc.visitors

import wacc.constructs._
import wacc.{SymbolTable, TestUtilities, VariableReference}

class StructVisitorTest extends VisitorTest {

  it should "struct type declaration should be valid" in {
    val parser = TestUtilities.setupParser("struct car")
    SymbolTable.structsTable += "car" -> Struct("car", Seq(VariableReference("a", Integer, 0), VariableReference("b", Character, 0)))
    val result = TestUtilities.buildSubProgram(parser.structType, TypeVisitor)

    result shouldBe StructType("car", Seq(("a", Integer), ("b", Character)))
  }

  "Visiting a struct" should "create struct construct with the correct symbol table" in {
    // Needs to be parsed with program because we declare all functions before parsing the bodies
    val parser = TestUtilities.setupParser("struct car int a; char b;")
    val result = TestUtilities.buildSubProgram(parser.struct, StructVisitor)

    val struct = result.right.get._1
    struct.identifier shouldBe "car"
    struct.members.head shouldBe VariableReference("a", Integer, 0)
    struct.members(1) shouldBe VariableReference("b", Character, 4)
  }

  it should "add the struct reference to the symbol table" in {
    val parser = TestUtilities.setupParser("struct car int a; char b;")
    val result = TestUtilities.buildSubProgram(parser.struct, StructVisitor)

    SymbolTable.structsTable.get("car") should be (defined)
  }

  it should "be a semantic error if you declare the same struct twice" in {
    val parser = TestUtilities.setupParser("begin struct car int a; char b; struct car int z; skip end")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result.left.value should (be (a[SemanticError]) or be (a[List[SemanticError]]))
  }

}
