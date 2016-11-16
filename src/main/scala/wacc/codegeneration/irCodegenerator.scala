package wacc.codegeneration

import wacc.constructs._

/**
  * Created by panayiotis on 15/11/16.
  */
//accumulator register approach
class irCodegenerator {
  val allRegisters = Seq(R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14)

  def codegen(program: Program): Seq[Instruction] = {
    transNext(program, allRegisters)
  }


  def transNext(a: Any, registers: Seq[Register]): Seq[Instruction] = {
    a match {
      case Program(functions, stmt)
        => transProgram(functions, stmt, registers)
      case Function(ident, params, vartype, stmt)
        => transFunction(ident, params, vartype, stmt, registers)
      case DeclareStatement(vartype: Type, identifier: String, value: AssignValue)
        => transDeclareStatement(vartype, identifier, value, registers)
      case stmt:Statement
        => transStatement(stmt, registers)
      case default
        => println("can we even reach this point"); Seq[Instruction]()
    }
  }

  def transProgram(functions: Seq[Function], stmts: Seq[Statement], registers: Seq[Register]): Seq[Instruction] = {
    val instruction = Seq[Instruction]()
    for (f <- functions) instruction ++ transNext(f, registers)
    for (stmt <- stmts) instruction ++ transNext(stmt, registers)
    instruction
  }

  def transFunction(ident: String, params: Seq[Param], vartype: Type, stmt: Seq[Statement], registers: Seq[Register]): Seq[Instruction] = {
    val instruction = Seq[Instruction]()
    instruction
  }

  def transStatement(stmt: Statement, registers: Seq[Register]) = {
    stmt match {
      case DeclareStatement(vartype: Type, identifier: String, value: AssignValue)
      => transDeclareStatement(vartype, identifier, value, registers)
      case AssignStatement(lhs: AssignTarget, rhs: AssignValue)
      => transAssignStatement(lhs, rhs, registers)
      case ExitStatement(exitCode: Expression)
      => transExitStatement(exitCode, registers)
      case ReturnStatement(returnValue: Expression)
      => transReturnStatement(returnValue, registers)
    }
  }

  def transDeclareStatement(vartype: Type, identifier: String, value: AssignValue, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = Seq[Instruction]()
    println("declare " + identifier + " to be " + value + "(" + vartype + ")" )
    instruction
  }

  def transAssignStatement(lhs: AssignTarget, rhs: AssignValue, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = Seq[Instruction]()
    println("assign " + rhs + " to " + lhs)
    instruction
  }

  def transExitStatement(exitCode: Expression, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = Seq[Instruction]()
    println("Exiting with code " + exitCode)
    instruction
  }

  def transReturnStatement(returnValue: Expression, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = Seq[Instruction]()
    println("Returning with value " + returnValue)
    instruction
  }

}
