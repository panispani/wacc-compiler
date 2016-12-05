package wacc.codegeneration

import wacc.arm._
import wacc.constructs._

object TransFunctions {
  def transFunction(function: Function, registers: Seq[Register]): Seq[Instruction] = {
    val (beginFrame, endFrame) = Macros.frame(function.symbolTable.sizeInBytes, isBranch = true)

    CodeSegment()
       .extend(DefineLabel(Label(function.identifier)))
       .extend(beginFrame)
       .extend(function.statements flatMap (s => s.transStatement(registers).instructions) : _*)
       .extend(endFrame)
      .instructions

  }
}
