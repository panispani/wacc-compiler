package wacc.visitors

import wacc.SymbolTable
import wacc.constructs._

class StatementVisitorTest extends VisitorTest {

  "Visiting exit" should "create an exit statement" in {
    val parser = TestUtilities.setupParser("exit 10")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be (ExitStatement(IntegerLiteral(10)))
  }

  it should "be a semantic error when the exit code expression evaluates to a non-integer type" in {
    val parser = TestUtilities.setupParser("exit true")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.left.value should be (SemanticError("Exit statement code should evaluate to value of type int"))
  }

  it should "be a semantic error when identifier is not declared" in {
    val parser = TestUtilities.setupParser("exit x")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.left.value should be (SemanticError("Identifier x not declared"))
  }

  it should "create an exit statement with integer expression exit codes" in {
    val parser = TestUtilities.setupParser("exit 2 + 3")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be (ExitStatement(BinaryOperatorExpr(IntegerLiteral(2), PlusBinOp, IntegerLiteral(3))))
  }

  it should "allow re-declarations in new scopes" in {
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
        DeclareStatement(Integer,"b",VariableReferenceExpression(Integer))
      )
    )
  }



}
