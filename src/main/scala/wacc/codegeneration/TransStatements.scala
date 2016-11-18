package wacc

import wacc.TransAssigns._
import wacc.TransExpressions._
import wacc.codegeneration._
import wacc.constructs._

/**
  * Created by panayiotis on 16/11/16.
  */
package object TransStatements {

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
      case SkipStatement()
      => Seq()
    }
  }

  def transDeclareStatement(vartype: Type, identifier: String, value: AssignValue, registers: Seq[Register]): Seq[Instruction] = {
    println("declare " + identifier + " to be " + value + "(" + vartype + ")" )
    val bytes = vartype match {
      case PrimitiveType("int") => 4
      case PrimitiveType("bool") => 1
      case PrimitiveType("char") => 1
      case PrimitiveType("string") => 4 //keep on heap, this is a pointer
      case ArrayType(elemtype: Type) => 4 //keep on heap
      case PairType(ftype, sType) => 4 //keep on heap
    }
    VarLog.add(identifier, bytes, vartype)
    val store =  vartype match {
      case PrimitiveType("int") => Seq(STR(registers.head, RegisterAddress(SP)))
      case PrimitiveType("bool") => Seq(STR(registers.head, RegisterAddress(SP)))
      case PrimitiveType("char") => Seq(STRB(registers.head, RegisterAddress(SP)))
      case default => println("not impelemented"); Seq()
    }
    //result on first register in list
    transAssignRhs(value, registers) ++ store
  }

  def transAssignStatement(lhs: AssignTarget, rhs: AssignValue, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = Seq[Instruction]()
    println("assign " + rhs + " to " + lhs)
    instruction
  }

  def transExitStatement(exitCode: Expression, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = transExpression(exitCode, registers)

    instruction ++ Seq(MOV(R0, RegisterOperand(registers.head)), BL(Label("exit")))
  }

  def transReturnStatement(returnValue: Expression, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = transExpression(returnValue, registers)

    instruction ++ Seq(MOV(R0, RegisterOperand(registers.head)), BL(Label("exit")))
  }

  def transStatementSequence(seq: Seq[Statement], registers: Seq[Register]): Seq[Instruction] = {
    val instructions
    = for {
      stmt <- seq
    } yield transStatement(stmt, registers)
    instructions.flatten
  }
}
