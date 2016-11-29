package wacc.codegeneration

import wacc.arm._
import wacc.constructs._

object TransFunctions {
  def transFunction(function: Function, registers: Seq[Register]): Seq[Instruction] = {
    val (beginFrame, endFrame) = Macros.frame(function.symbolTable.sizeInBytes, isBranch = true)
    CodeSegment()
      .append(DefineLabel(Label(function.identifier)))

      .extend(beginFrame)
      .append(PUSH(Seq(FSP)))
      .append(MOV(FP,SP))
      .append(MOV(FSP, SP))
      .extend(function.statements flatMap (s => TransStatements.transStatement(s, function.symbolTable, registers)))
      .extend(endFrame)
      .append(POP(Seq(FSP)))
      .append(POP(Seq(PC)))
      .instructions
  }
}
