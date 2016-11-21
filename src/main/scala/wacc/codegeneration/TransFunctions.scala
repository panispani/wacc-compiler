package wacc

import wacc.codegeneration._
import wacc.constructs._
import wacc.TransStatements._

/**
  * Created by panayiotis on 16/11/16.
  */
package object TransFunctions {
  def transFunction(function: Function, registers: Seq[Register]): Seq[Instruction] = {
    new CodeSegment()
      .append(DefineLabel(Label(function.identifier)))
      .extend(function.statements flatMap (s => transStatement (s, registers)))
      .append(RETURN)
      .instructions
  }
}
