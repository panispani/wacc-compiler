package wacc

import wacc.constructs._
import wacc.codegeneration._
import TransStatements._
import TransFunctions._
import TransPrograms._
import TransAssigns._
import TransExpressions._

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
