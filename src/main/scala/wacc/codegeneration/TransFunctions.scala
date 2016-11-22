package wacc

import wacc.codegeneration._
import wacc.constructs._
import wacc.TransStatements._

package object TransFunctions {
  def transFunction(function: Function, registers: Seq[Register]): Seq[Instruction] = {
    new CodeSegment()
      .append(DefineLabel(Label(function.identifier)))
      .append(NEW_STACK_FRAME)
      .append(SUB(SP, SP, ImmOperand(function.symbolTable.sizeInBytes)))
      .extend(function.statements flatMap (s => transStatement (s, function.symbolTable, registers)))
      .append(ADD(SP, SP, ImmOperand(function.symbolTable.sizeInBytes)))
      .append(RETURN)
      .instructions
  }
}
