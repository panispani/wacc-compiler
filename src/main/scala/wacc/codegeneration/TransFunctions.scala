package wacc.codegeneration

import wacc.arm._
import wacc.constructs._

object TransFunctions {
  def transFunction(function: Function, registers: Seq[Register]): Seq[Instruction] = {
    val beginFrame = Macros.functionCallFrameStart(function.symbolTable.sizeInBytes)
    CodeSegment(DefineLabel(Label(function.identifier)))
      .extend(beginFrame)
      .extend(function.statements.map(_.transStatement(registers)).foldLeft(CodeSegment())((acc, x) => acc.extend(x)))
      .extend(POP(Seq(PC)))
      .instructions
  }
}
