package wacc.codegeneration

import wacc.SymbolTable
import wacc.arm._
import wacc.constructs._

/**
  * Created by panayiotis on 16/11/16.
  */
object TransStatements {

  def transStatement(statement: Statement, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    statement.transStatement(symbolTable, registers).instructions
  }

  def transStatementSequence(seq: Seq[Statement], symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    val instructions = for {
      stmt <- seq
    } yield transStatement(stmt, symbolTable, registers)

    CodeSegment()
      .extend(instructions.flatten : _*)
      .instructions
  }
}
