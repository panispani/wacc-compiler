package wacc

import wacc.TransFunctions._
import wacc.TransStatements._
import wacc.constructs._
/**
  * Created by panayiotis on 16/11/16.
  */
package object codegeneration {

  def transProgram(functions: Seq[Function], stmts: Seq[Statement], registers: Seq[Register]): Seq[Instruction] = {
    val functionInstructions =
      for {
        f <- functions
      } yield transNext(f, registers)

    val mainInstructions = transStatementSequence(stmts, registers)

    val stackBytes = VarLog.byteCount()
    // add labels later
    functionInstructions.flatten ++
      Seq(SUB(SP, SP, ImmOperand(stackBytes))) ++
      mainInstructions ++
      Seq(ADD(SP, SP, ImmOperand(stackBytes)), MOV(R0, ImmOperand(0)))
  }

  def transNext(a: Any, registers: Seq[Register]): Seq[Instruction] = {
    a match {
      case Program(functions, stmt)
      => {
//        transProgram(functions, stmt, registers)
        val functionDefinitions = functions flatMap(transNext(_, registers))
        val statementInstructions = stmt flatMap(transNext(_, registers))
        functionDefinitions ++ statementInstructions
      }
      case Function(ident, params, vartype, stmt)
      => transFunction(ident, params, vartype, stmt, registers)
      case stmt:Statement
      => transStatement(stmt, registers)
      case default
      => println("can we even reach this point"); Seq[Instruction]()
    }
  }
}
