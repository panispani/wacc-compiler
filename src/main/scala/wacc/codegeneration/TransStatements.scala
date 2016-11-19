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
      case DeclareStatement(vartype: Type, variable: VariableReference, value: AssignValue)
      => transDeclareStatement(vartype, variable, value, registers)
      case AssignStatement(lhs: AssignTarget, rhs: AssignValue)
        => transAssignStatement(lhs, rhs, registers)

      case ExitStatement(exitCode: Expression)      => transExitStatement(exitCode, registers)
      case ReturnStatement(returnValue: Expression) => transReturnStatement(returnValue, registers)
      case SkipStatement()                          => Seq()
      case PrintStatement(expression)               => transExpression(expression, registers) :+ BL(Label("p_print_string"))
      case PrintLnStatement(expression)             => transExpression(expression, registers) :+ BL(Label("p_print_ln"))
    }
  }

  def transDeclareStatement(vartype: Type, identifier: VariableReference, value: AssignValue, registers: Seq[Register]): Seq[Instruction] = {
    println("declare " + identifier + " to be " + value + "(" + vartype + ")")

    // TODO: See what this should do
    //    VarLog.add(identifier, vartype)

    //result on first register in list
    val instruction = transAssignRhs(value, registers)

    // will be in the tree TODO
    val offset = 0
    //val offset = SymbolTable.currentTable.lookupMemoryObject(identifier).get.offset

    val store =  vartype match {
      case Integer => Seq(STR(registers.head, RegisterAddress(SP, offset)))
      case Boolean | Character => Seq(STRB(registers.head, RegisterAddress(SP, offset)))
      case default => println("not impelemented"); Seq()
    }

    //TODO: code to update the identifier in the symbol table with the memory location

    instruction ++ store
  }

  def transAssignStatement(lhs: AssignTarget, rhs: AssignValue, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = transAssignRhs(rhs, registers)
    println("assign " + rhs + " to " + lhs)
    instruction
  }

  def transExitStatement(exitCode: Expression, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = transExpression(exitCode, registers)

    instruction ++ Seq(MOV(R0, RegisterOperand(registers.head)), BL(Label("exit")))
  }

  def transReturnStatement(returnValue: Expression, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = transExpression(returnValue, registers)

    instruction ++ Seq(MOV(R0, RegisterOperand(registers.head)))
  }

  def transStatementSequence(seq: Seq[Statement], registers: Seq[Register]): Seq[Instruction] = {
    val instructions
    = for {
      stmt <- seq
    } yield transStatement(stmt, registers)
    instructions.flatten
  }
}
