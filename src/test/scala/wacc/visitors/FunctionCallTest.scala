package wacc.visitors

import wacc.constructs._

/**
  * Created by panayiotis on 10/11/16.
  */
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

}
