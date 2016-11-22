package wacc

import wacc.TransExpressions._
import wacc.codegeneration._
import wacc.arm.{ConditionalInstruction, Instruction}
import wacc.constructs._

/**
  * Created by panayiotis on 16/11/16.
  */
package object TransAssigns {

  def transAssignRhs(value: AssignValue, registers: Seq[Register]): Seq[Instruction] = {
    value match {
      case e: Expression => transExpression(e, registers)
      case default  => println("not implemented"); Seq()
    }
  }
}
