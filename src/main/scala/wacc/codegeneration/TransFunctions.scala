package wacc

import wacc.TransStatements._
import wacc.codegeneration._
import wacc.arm._
import wacc.constructs._

package object TransFunctions {
  def transFunction(function: Function, registers: Seq[Register]): Seq[Instruction] = {
    val (beginFrame, endFrame) = Macros.frame(function.symbolTable.sizeInBytes, isBranch = true)
    new CodeSegment()
      .append(DefineLabel(Label(function.identifier)))

      .extend(beginFrame)
      .extend(function.statements flatMap (s => transStatement (s, function.symbolTable, registers)))
      .extend(endFrame)

      .append(RETURN)
      .instructions
  }
}
