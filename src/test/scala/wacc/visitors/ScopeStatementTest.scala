package wacc.visitors

import wacc.constructs._
import wacc.{TestUtilities, VariableReference}

class ScopeStatementTest extends VisitorTest {
  "Visiting a new scope" should "allow re-declarations in new scopes" in {
    val parser = TestUtilities.setupParser("int a = 1; begin bool b = true; int a = 2 end")
    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)

    result.right.value.head should be (DeclareStatement(
      Integer,
      VariableReference("a", Integer, -4),
      IntegerLiteral(1)))

    result.right.value(1) should be (a[ScopeStatement])
    val scope = result.right.value(1).asInstanceOf[ScopeStatement]

    scope.statements(1) should be (DeclareStatement(
              Integer,
              VariableReference("a", Integer, -5),
              IntegerLiteral(2))
          )

    scope.symbolTable.lookup("a") should be (defined)
  }

  it should "use parent variables after exiting a scope" in {
    val parser = TestUtilities.setupParser("int a = 1; begin int x = 1; int a = 2 end; int b = a")
    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)

    result.right.value.last should be (DeclareStatement(
          Integer,
          VariableReference("b", Integer, -8),
          VariableReference("a", Integer, -4))
      ) 
  }

  it should "use parent variables when exiting a scope and not allow redeclarations" in {
    val parser = TestUtilities.setupParser("int a = 1; begin int a = 2 end; int a = 3")
    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)

    result.left.value shouldBe a[SemanticError]
  }

}
