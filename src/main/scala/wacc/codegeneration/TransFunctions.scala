package wacc.codegeneration

import wacc.arm._
import wacc.constructs._

object TransFunctions {
  def transFunction(function: Function, registers: Seq[Register]): Seq[Instruction] = {
    val (beginFrame, endFrame) = Macros.semanticFrame(function.symbolTable.sizeInBytes)
    CodeSegment()
      .extend(DefineLabel(Label(function.identifier)))
      .extend(PUSH(Seq(LR)))
      .extend(beginFrame)
      .extend(PUSH(Seq(FSP)))
      .extend(MOV(FP,SP))
      .extend(MOV(FSP, SP))
      .extend(function.statements flatMap (s => TransStatements.transStatement(s, registers)))
      .extend(endFrame)
      .extend(POP(Seq(FSP)))
      .extend(POP(Seq(PC)))
      .instructions
  }
}
