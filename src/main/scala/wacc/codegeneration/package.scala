package wacc

import wacc.TransFunctions._
import wacc.TransStatements._
import wacc.constructs._

/**
  * Created by panayiotis on 16/11/16.
  */
package object codegeneration {
  def transProgram(functions: Seq[Function], stmts: Seq[Statement], registers: Seq[Register]): Seq[Instruction] = {
    val instruction = Seq[Instruction]()
    val functionInstructions =
      for {
        f <- functions
      } yield transNext(f, registers)

    val mainInstructions =
      for {
        stmt <- stmts
      } yield transNext(stmt, registers)

    val stackBytes = VarLog.byteCount()
    // add labels later
    functionInstructions.flatten ++
      Seq(SUB2(SP, SP, stackBytes)) ++
      mainInstructions.flatten ++
      Seq(ADDS(SP, SP, stackBytes), MOVS(R0, 0))
  }

  def transNext(a: Any, registers: Seq[Register]): Seq[Instruction] = {
    a match {
      case Program(functions, stmt)
      => transProgram(functions, stmt, registers)
      case Function(ident, params, vartype, stmt)
      => transFunction(ident, params, vartype, stmt, registers)
      case DeclareStatement(vartype, identifier, value)
      => transDeclareStatement(vartype, identifier, value, registers)
      case stmt:Statement
      => transStatement(stmt, registers)
      case default
      => println("can we even reach this point"); Seq[Instruction]()
    }
  }
}
