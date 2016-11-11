package wacc.visitors

import wacc.constructs._

class FunctionCallTest extends VisitorTest{

  "Making function calls" should "succeed when empty argument list" in {
    val parser = TestUtilities.setupParser("begin int foo() is return 1 end int a = call foo() end ")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result.right.value should be
      Program(
        List(
          Function(
            "foo",
            List(),
            PrimitiveType("int"),
            List(ReturnStatement(IntegerLiteral(1)))
          )
        ),
        List(
          DeclareStatement(
            PrimitiveType("int"),
            "a",
            FunctionCall(
              "foo",
              List(),
              PrimitiveType("int")
            )
          )
        )
      )
  }

  it should "succeed when an argument has tha same name as the function" in {
    val parser = TestUtilities.setupParser("begin\n int f(int f) is\n return f\n end\n int f = call f(99);\n println f\nend")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result.right.value shouldBe a[Program]
  }

  it should "succeed when the argument types match" in {
    val parser = TestUtilities.setupParser("begin int foo(int a) is return a end int a = call foo(2) end")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result.right.value should be(
      Program(
        List(
          Function(
            "foo",
            List(Param(Variable("a",PrimitiveType("int")))),
            PrimitiveType("int"),
            List(
              ReturnStatement(VariableReferenceExpression("a", PrimitiveType("int"))))
            )
          ),
          List(
            DeclareStatement(
              PrimitiveType("int"),
              "a",
              FunctionCall(
                "foo",
                List(IntegerLiteral(2)),
                PrimitiveType("int")
              )
            )
          )
        )
    )
  }

  it should "fail when argument types don't match" in {
    val parser = TestUtilities.setupParser("begin int foo(int a) is return a end int a = call foo('a') end")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result.left.value shouldBe a[SemanticError]
  }

  it should "fail when argument number doesn't match the number of parameters" in {
    val parser = TestUtilities.setupParser("begin int foo(int a) is return a end int a = call foo(1, 2) end")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result.left.value shouldBe a[SemanticError]
  }

  it should "fail when function return type and variable on lhs don't line up" in {
    val parser = TestUtilities.setupParser("begin int foo(int a) is return 1 end char a = call foo(1) end")
    val result = TestUtilities.buildSubProgram(parser.program, ProgramVisitor)

    result.left.value shouldBe a[SemanticError]
  }
}
