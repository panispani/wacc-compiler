package wacc.visitors

import wacc.constructs.{SemanticError, _}

/**
  * Created by panayiotis on 10/11/16.
  */
class ScopeStatementTest extends VisitorTest {
  "Visiting a new scope" should "allow re-declarations in new scopes" in {
    val parser = TestUtilities.setupParser("int a = 1; begin int a = 2 end")
    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)

    result.right.value should be (
      List(
        DeclareStatement(Integer,"a",IntegerLiteral(1)),
        ScopeStatement(
          List(
            DeclareStatement(Integer, "a",IntegerLiteral(2))
          )
        )
      )
    )
  }

  it should "use parent variables after exiting a scope" in {
    val parser = TestUtilities.setupParser("int a = 1; begin int a = 2 end; int b = a")
    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)

    result.right.value should be (
      List(
        DeclareStatement(Integer,"a",IntegerLiteral(1)),
        ScopeStatement(
          List(
            DeclareStatement(Integer, "a",IntegerLiteral(2))
          )
        ),
        DeclareStatement(Integer,"b",VariableReferenceExpression("a", Integer))
      )
    )
  }

  it should "use parent variables when exiting a scope and not allow redeclarations" in {
    val parser = TestUtilities.setupParser("int a = 1; begin int a = 2 end; int a = 3")
    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)

    result.left.value shouldBe a[SemanticError]
//    result.left.value should be (SemanticError("Re-declaration of variable a"))
  }

}
