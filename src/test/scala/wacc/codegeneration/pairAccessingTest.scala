package wacc.codegeneration

import wacc.arm._
import wacc.visitors.StatementVisitor
import wacc.{SymbolTable, TestUtilities}

class pairAccessingTest extends CodeGenTest {

  it should "be able to handle declaring an int with assign value another variable" in {
    val parser = TestUtilities.setupParser("pair(int, int) p = null")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    val parser2 = TestUtilities.setupParser("fst p = 1")
    val program2 = TestUtilities.buildSubProgram(parser2.statement, StatementVisitor)

    val availableRegisters = Seq(R4, R5, R6, R7, R8)
    val instructions = TransStatements.transStatementSequence(Seq(program.right.get, program2.right.get), availableRegisters)
  }

  it should "to handle declaring an int with assign value another variable" in {
    val parser = TestUtilities.setupParser("pair(pair, pair) p = null")
    val program = TestUtilities.buildSubProgram(parser.statement, StatementVisitor)

    val parser2 = TestUtilities.setupParser("println p")
    val program2 = TestUtilities.buildSubProgram(parser2.statement, StatementVisitor)

    val availableRegisters = Seq(R4, R5, R6, R7, R8)
    val instructions = TransStatements.transStatementSequence(Seq(program.right.get, program2.right.get), availableRegisters)
  }

}
