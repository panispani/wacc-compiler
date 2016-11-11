package wacc.visitors

import wacc.constructs.{Function, Integer, IntegerLiteral, ReturnStatement, SemanticError, SyntaxError}
import wacc.{FunctionReference, SymbolTable}

/**
  * Created by ema on 09/11/2016.
  */
class FunctionVisitorTest extends VisitorTest {

  "Visiting a function" should "create function construct" in {
    val parser = TestUtilities.setupParser("int f() is return 3 end")
    val result = TestUtilities.buildSubProgram(parser.function, FunctionVisitor)

    result.right.value should be (Function("f", Seq(), Integer, Seq(ReturnStatement(IntegerLiteral(3)))))
  }

  it should "add the function reference to the symbol table" in {
    val parser = TestUtilities.setupParser("int f() is return 3 end")
    val result = TestUtilities.buildSubProgram(parser.function, FunctionVisitor)

    SymbolTable.globalTable.lookup("f") should contain (FunctionReference(Integer, Seq()))
  }

  it should "be a semantic error if two or more parameters have the same name" in {
    val parser = TestUtilities.setupParser("char f(int a, char a) is return 'c' end")
    val result = TestUtilities.buildSubProgram(parser.function, FunctionVisitor)

    result.left.value should be (SemanticError("A function shouldn't have two or more parameters with the same"))
  }

  it should "be valid when the last statement is exit" in {
    val parser = TestUtilities.setupParser("char f() is exit 1 end")
    val result = TestUtilities.buildSubProgram(parser.function, FunctionVisitor)

    result.right.value shouldBe a[Function]
  }

  it should "be a syntax error if the last statement is not a return" in {
    val parser = TestUtilities.setupParser("int f() is bool b = true end")
    val result = TestUtilities.buildSubProgram(parser.function, FunctionVisitor)

    result.left.value should be (SyntaxError("The last statement of a function should be a return"))
  }

  it should "be a syntax error if the last statement is not a return with conditionals" in {
    val parser = TestUtilities.setupParser("int f() is bool b = true ; if b then return 1 else println \"wrong\" fi end")
    val result = TestUtilities.buildSubProgram(parser.function, FunctionVisitor)

    result.left.value should be (SyntaxError("The last statement of a function should be a return"))
  }

  it should "be a syntax error if the last statement is not a return with loops" in {
    val parser = TestUtilities.setupParser("" +
      "int f() is " +
      "bool b = true ;" +
      "if b then return 1" +
      "else while true do skip done fi end")

    val result = TestUtilities.buildSubProgram(parser.function, FunctionVisitor)

    result.left.value should be (SyntaxError("The last statement of a function should be a return"))
  }

  it should "be a semantic error if there is a mismatch between the declared and the actual return type" in {
    val parser = TestUtilities.setupParser("int f() is return 'c' end")
    val result = TestUtilities.buildSubProgram(parser.function, FunctionVisitor)

    result.left.value should be (SemanticError("The actual return type of a function should match the declared one"))
  }

}
