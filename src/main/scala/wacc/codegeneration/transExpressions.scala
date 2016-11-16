package wacc

import wacc.codegeneration.Weight._
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
package object transExpressions{
  def transExpression(expr: Expression, registers: Seq[Register]): Seq[Instruction] = {
    expr match {
      case BinaryOperatorExpr(e1, bOp, e2) => {
        if (weight(e1) > weight(e2)) {
          // e1 first
        } else {
          // e2 first
        }
        Seq()
      }
      case IntegerLiteral(value) => Seq(MOVS(registers.head, value))
      case BoolLiteral(value) => val v = if (value) 1 else 0
        Seq(MOVS(registers.head, v))
      case CharLiteral(value) => Seq(MOVCH(registers.head, value))
    }
  }

}
