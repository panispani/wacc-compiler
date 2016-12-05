package wacc.visitors

import wacc.{SymbolTable, TestUtilities, VariableReference}
import wacc.constructs._

class LoopStatementTest extends VisitorTest {
  "A while loop statement" should "allow re-declarations in its new scope" in {
    val parser = TestUtilities.setupParser("int x = 1; while true do int x = 1 done")
    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)

    result.right.value(1) should be (a[LoopStatement])
    val loop = result.right.value(1).asInstanceOf[LoopStatement]

    loop.symbolTable.lookup("x") shouldBe defined
  }

  "A do-while statement" should "be possible" in {
    val parser = TestUtilities.setupParser("int x = 1; do int x = 1 while true")
    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)

    result.right.value(1) should be (a[LoopStatement])
    val loop = result.right.value(1).asInstanceOf[LoopStatement]

    loop.doWhile shouldBe true
  }

  "For loop" should "be parsed properly" in {
    val parser = TestUtilities.setupParser(
      "for int i = 1; i < 2; i = i + 1 do skip done")

    val result = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    result.right.value should be (a[ForLoopStatement])
    val loop = result.right.value.asInstanceOf[ForLoopStatement]

    loop.init shouldBe DeclareStatement(
      Integer,
      VariableReference("i", Integer, -4),
      IntegerLiteral(1))

    loop.cond shouldBe BinaryOperatorExpr(
      VariableReference("i", Integer, -4),
      LtBinOp,
      IntegerLiteral(2))

    loop.step shouldBe AssignStatement(
      VariableReference("i", Integer, -4),
      BinaryOperatorExpr(
        VariableReference("i", Integer, -4),
        PlusBinOp,
        IntegerLiteral(1)
      )
    )
  }

  it should "allow re-declaring variable from parent scope in init" in {
    // init means the first statement of the for loop which must be a declaration
    val parser = TestUtilities.setupParser(
      "bool i = false; int x = 5; for int i = 1; i < 2; i = i + 1 do skip done")

    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)
    val loop = result.right.value(2).asInstanceOf[ForLoopStatement]

    val loopVariable = VariableReference("i", Integer, -9)

    // i from loop scope
    loop.init.asInstanceOf[DeclareStatement].newReference should be (loopVariable)

    // checks that step and cond use loop variable
    loop.step.asInstanceOf[AssignStatement].lhs should be (loopVariable)
    loop.cond.asInstanceOf[BinaryOperatorExpr].expression1 should be (loopVariable)

    // i from parent scope
    SymbolTable().lookup("i").get should be (VariableReference("i", Boolean, -1))
  }

  it should "not allow re-declaration of init variable in loop body" in {
    val parser = TestUtilities.setupParser(
      "for int i = 1; i < 2; i = i + 1 do int i = 5 done")

    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)
    result.left.get should be (a[SemanticError])
  }

  // We used to allow declare statements only but there's no point to restrict this
  it should "allow non-declare/assign statements as init/step" in {
    val parser = TestUtilities.setupParser(
      "for print \"ok\"; true; print \"step\" do skip done")

    val result = TestUtilities.buildSubProgram(parser.sequence, SequenceVisitor)
    result.isRight should be (true)
  }
}