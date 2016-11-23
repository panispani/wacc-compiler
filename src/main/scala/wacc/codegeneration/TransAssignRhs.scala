package wacc

import wacc.TransExpressions._
import wacc.codegeneration._
import wacc.arm.{ConditionalInstruction, Instruction, Register}
import wacc.constructs._

/**
  * Created by panayiotis on 16/11/16.
  */
package object TransAssignRhs {

  def transAssignRhs(value: AssignValue, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    value match {
      case e: Expression => transExpression(e, symbolTable, registers)
      case default  => println("not implemented"); Seq()
    }
  }
}
