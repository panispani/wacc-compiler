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
    val argumentsSize = ImmOperand(fc.args.map(_.vartype.size).sum)
    CodeSegment()
      //.append(SUB(SP, SP, argumentsSize))
      .extend(fc.args flatMap (e => transExpression(e, symbolTable, registers) :+ STR(registers.head, RegisterAddress(SP, -e.vartype.size, writeback = true)))) //PUSH(Seq(registers.head)))) // Evaluate all arguments and push them on stack
      .append(BL(Label(fc.identifier)))
      .append(ADD(SP, SP, argumentsSize))
      .append(MOV(registers.head, R0))
      .instructions
  }
}
