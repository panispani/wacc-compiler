package wacc.visitors

import wacc.constructs._
import wacc.{FunctionReference, SymbolTable, TestUtilities, VariableReference}

class FunctionVisitorTest extends VisitorTest {

  "Visiting a function" should "create function construct with the correct symbol table" in {
    // Needs to be parsed with program because we declare all functions before parsing the bodies
    val parser = TestUtilities.setupParser("begin int f() is int x = 1; return 1 end int a = 1 end")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    val function = result.right.value.functions.head
    function.identifier should be ("f")
    function.params should be (empty)
    function.vartype should be (Integer)

    function.statements should be (Seq(
      DeclareStatement(Integer, VariableReference("x", Integer, 0), IntegerLiteral(1)),
      ReturnStatement(IntegerLiteral(1))))

    function.symbolTable.lookup("x") should be (defined)
    function.symbolTable.lookup("a") should not be defined
  }

  it should "add the function reference to the symbol table" in {
    val parser = TestUtilities.setupParser("begin int f() is return 3 end skip end")
    TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    SymbolTable.globalTable.lookup("f") should contain (FunctionReference("f", Integer, Seq()))
  }

  it should "be a semantic error if two or more parameters have the same name" in {
    val parser = TestUtilities.setupParser("begin char f(int a, char a) is return 'c' end skip end")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result.left.value should (be (a[SemanticError]) or be (a[List[_]]))
  }

  it should "be valid when the last statement is exit" in {
    val parser = TestUtilities.setupParser("begin char f() is exit 1 end skip end")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result.right.value.functions shouldBe a[List[_]]
  }

  it should "be a syntax error if the last statement is not a return" in {
    val parser = TestUtilities.setupParser("begin int f() is bool b = true end skip end")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result.left.value should (be (a[SyntaxError]) or be (a[List[_]]))
  }

  it should "be a syntax error if the last statement is not a return with conditionals" in {
    val parser = TestUtilities.setupParser("begin int f() is bool b = true ; if b then return 1 else println \"wrong\" fi end skip end")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result.left.value should (be (a[SyntaxError]) or be (a[List[_]]))
  }

  it should "be a syntax error if the last statement is not a return with loops" in {
    val parser = TestUtilities.setupParser("begin " +
      "int f() is " +
      "bool b = true ;" +
      "if b then return 1" +
      "else while true do skip done fi end "+
      "skip end")

    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result.left.value should (be (a[SyntaxError]) or be (a[List[_]]))
  }

  it should "be a semantic error if there is a mismatch between the declared and the actual return type" in {
    val parser = TestUtilities.setupParser("begin int f() is return true end skip end")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result.left.value should (be (a[SemanticError]) or be (a[List[_]]))
  }

  it should "be a semantic error if a function with the same name already exists" in {
    val parser = TestUtilities.setupParser("begin int f() is return 1 end char f() is return 'a' end skip end")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result.left.value should (be (a[SemanticError]) or be (a[List[_]]))
  }

}
