package wacc

import wacc.constructs._
import wacc.codegeneration._
import transStatements._
import transFunctions._
import transPrograms._
import transAssigns._
import transExpressions._

/**
  * Created by panayiotis on 16/11/16.
  */
package object transAssigns {

  def transAssignRhs(value: AssignValue, registers: Seq[Register]): Seq[Instruction] = {
    value match {
      case e: Expression => transExpression(e, registers)
      case default  => println("not implemented"); Seq()
    }
  }
}
