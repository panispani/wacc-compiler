package wacc

import wacc.TransStatements._
import wacc.codegeneration._
import wacc.arm._
import wacc.constructs._

package object TransFunctions {
  def transFunction(function: Function, registers: Seq[Register]): Seq[Instruction] = {
    new CodeSegment()
      .append(DefineLabel(Label(function.identifier)))
      .append(NEW_STACK_FRAME)
      .append(PUSH(Seq(BP))) // R11 will act as the frame pointer
      .append(MOV(BP, SP))
      .append(SUB(SP, SP, ImmOperand(function.symbolTable.sizeInBytes)))
      .extend(function.statements flatMap (s => transStatement (s, function.symbolTable, registers)))
      .append(ADD(SP, SP, ImmOperand(function.symbolTable.sizeInBytes)))
      .append(POP(Seq(BP)))
      .append(RETURN)
      .instructions
  }
}
