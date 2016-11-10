package wacc.visitors

import wacc.SymbolTable
import wacc.constructs._
import wacc.visitors.TestUtilities.VisitorTest

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
    println(SymbolTable.currentTable.lookupAll("x"))
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.left.value should be (SemanticError("Identifier x not declared"))
  }

  it should "create an exit statement with integer expression exit codes" in {
    val parser = TestUtilities.setupParser("exit 2 + 3")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be (ExitStatement(BinaryOperatorExpr(IntegerLiteral(2), PlusBinOp, IntegerLiteral(3))))
  }

}
