package wacc.visitors

import wacc.constructs.{CharLiteral, IntegerLiteral, PrintStatement, StringLiteral}

/**
  * Created by panayiotis on 10/11/16.
  */
class PrintStatementTest extends VisitorTest {

  "Visiting a print statement" should "allow int arguments" in {
    val parser = TestUtilities.setupParser("print 3")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be (PrintStatement(IntegerLiteral(3)))
  }

  "Visiting a print statement" should "allow char arguments" in {
    val parser = TestUtilities.setupParser("print 'a'")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be (PrintStatement(CharLiteral('a')))
  }

  "Visiting a print statement" should "allow string arguments" in {
    val parser = TestUtilities.setupParser("print \"str\"")
    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be (PrintStatement(StringLiteral("\"str\"")))
  }
}
