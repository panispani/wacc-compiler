package wacc.codegeneration

import wacc.arm._
import wacc.constructs._

/**
  * Created by panayiotis on 16/11/16.
  */
object TransStatements {

  def transStatement(statement: Statement, registers: Seq[Register]): Seq[Instruction] = {
    statement.transStatement(registers).instructions
  }

  def transStatementSequence(seq: Seq[Statement], registers: Seq[Register]): Seq[Instruction] = {
    val instructions = seq.map(transStatement(_, registers))

    CodeSegment()
      .extend(instructions.flatten)
      .instructions
  }
}
