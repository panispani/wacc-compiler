package wacc

import wacc.TransAssigns._
import wacc.TransExpressions._
import wacc.codegeneration._
import wacc.constructs._

import scala.collection.GenTraversableOnce

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
      case ConditionalStatement(expression, trueStatements, falseStatements) => transConditionalStatement(expression, trueStatements, falseStatements, registers)
    }
  }

  def transArrayLiteral(literal: ArrayLiteral, registers: Seq[Register]): Seq[Instruction] = {
    var offset = 4
    var instructions: Seq[Instruction] = Seq()

    for (elem <- literal.elements) {
      instructions ++= transExpression(elem, registers.tail) :+ STR(registers(1), RegisterAddress(registers.head, offset))
      offset += literal.vartype.elemtype.size
    }

    instructions
  }

  def transDeclareStatement(vartype: Type, identifier: VariableReference, value: AssignValue, registers: Seq[Register]): Seq[Instruction] = {
    println("declare " + identifier + " to be " + value + "(" + vartype + ")")

    // TODO: See what this should do
    //    VarLog.add(identifier, vartype)

    //result on first register in list
    val instruction = transAssignRhs(value, registers)

    val offset = identifier.offset

    val store =  vartype match {
      case Integer => Seq(STR(registers.head, RegisterAddress(SP, offset)))
      case Boolean | Character => Seq(STRB(registers.head, RegisterAddress(SP, offset)))
      case ArrayType(elemsType) => value match {
        case literal @ ArrayLiteral(elements) => {
          val arraySize = 4 + elements.size * elemsType.size
          return Seq(
            LDR(R0, Const(arraySize)),
            BL(Label("malloc")),
            MOV(registers.head, RegisterOperand(R0)),
            LDR(registers(1), Const(elements.size)),
            STR(registers(1), RegisterAddress(registers.head, 0))
          ) ++ transArrayLiteral(literal, registers) :+ STR(registers.head, RegisterAddress(SP, 0))
        }
      }
      case default => println("not impelemented"); Seq()
    }

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

  def transConditionalStatement(expression: Expression, trueStatements: Seq[Statement], falseStatements: Seq[Statement], registers: Seq[Register]): Seq[Instruction] = {
    val L0: Label = LabelCreator.newLabel()
    val L1: Label = LabelCreator.newLabel()

    transExpression(expression, registers) ++
      Seq(CMP(registers.head, ImmOperand(0)),
          B(L0, EQ())) ++
      transStatementSequence(falseStatements, registers) ++
      Seq(B(L1), DefineLabel(L0)) ++
      transStatementSequence(trueStatements, registers) ++
      Seq(DefineLabel(L1))
  }

  def transStatementSequence(seq: Seq[Statement], registers: Seq[Register]): Seq[Instruction] = {
    val instructions
    = for {
      stmt <- seq
    } yield transStatement(stmt, registers)
    instructions.flatten
  }
}
