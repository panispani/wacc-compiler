package wacc

import wacc.TransExpressions._
import wacc.codegeneration._
import wacc.arm._
import wacc.constructs._

/**
  * Created by panayiotis on 16/11/16.
  */
package object TransAssignRhs {

  def transAssignRhs(value: AssignValue, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    value match {
      case e: Expression    => transExpression(e, symbolTable, registers)
      case fc: FunctionCall => transFunctionCall(fc, symbolTable, registers)
      case default  => println("not implemented"); Seq()
    }
  }

  def transFunctionCall(fc: FunctionCall, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    CodeSegment()
      .extend(fc.args flatMap (e => transExpression(e, symbolTable, registers) :+ PUSH(Seq(registers.head)))) // Evaluate all arguments and push them on stack
      .append(BL(Label(fc.identifier)))
      .append(MOV(registers.head, R0))
      .instructions
  }
}
