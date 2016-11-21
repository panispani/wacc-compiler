package wacc.visitors

import wacc.{TestUtilities, VariableReference}
import wacc.constructs._

class ScopeStatementTest extends VisitorTest {
  "Visiting a new scope" should "allow re-declarations in new scopes" in {
    val parser = TestUtilities.setupParser("int a = 1; begin int a = 2 end")
    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)

    result.right.value should matchPattern {
      case List(
        DeclareStatement(
          Integer,
          VariableReference("a", Integer, 0),
          IntegerLiteral(1)),
        ScopeStatement(
          List(
            DeclareStatement(
              Integer,
              VariableReference("a", Integer, 0),
              IntegerLiteral(2))
          ),
          _ // ignore symbol table
        )
      ) =>
    }
  }

  it should "use parent variables after exiting a scope" in {
    val parser = TestUtilities.setupParser("int a = 1; begin int a = 2 end; int b = a")
    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)

    result.right.value should matchPattern {
      case List(
        DeclareStatement(
          Integer,
          VariableReference("a", Integer, 0),
          IntegerLiteral(1)),
        ScopeStatement(
          List(
            DeclareStatement(
              Integer,
              VariableReference("a", Integer, 0),
              IntegerLiteral(2))
          ),
          _ // ignore symbol table
        ),
        DeclareStatement(
          Integer,
          VariableReference("b", Integer, 4),
          VariableReference("a", Integer, 0))
      ) =>
    }
  }

  it should "use parent variables when exiting a scope and not allow redeclarations" in {
    val parser = TestUtilities.setupParser("int a = 1; begin int a = 2 end; int a = 3")
    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)

    result.left.value shouldBe a[SemanticError]
  }

}
