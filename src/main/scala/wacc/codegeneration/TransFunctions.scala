package wacc

import wacc.TransStatements._
import wacc.codegeneration._
import wacc.arm._
import wacc.constructs._

/**
  * Created by panayiotis on 16/11/16.
  */
package object TransFunctions {
  def transFunction(function: Function, registers: Seq[Register]): Seq[Instruction] = {
    new CodeSegment()
      .append(DefineLabel(Label(function.identifier)))
      .append(NEW_STACK_FRAME)
      .extend(function.statements flatMap (s => transStatement (s, function.symbolTable, registers)))
      .append(RETURN)
      .instructions
  }
}
