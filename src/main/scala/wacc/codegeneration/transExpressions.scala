package wacc

import wacc.codegeneration.Weight._
import wacc.constructs._
import wacc.codegeneration._
import transStatements._
import transFunctions._
import transPrograms._
import transAssigns._
import transExpressions._
import transBinaryOperators._

/**
  * Created by panayiotis on 16/11/16.
  */
package object transExpressions{
  def transExpression(expr: Expression, registers: Seq[Register]): Seq[Instruction] = {
      registers match {
        case (r1::r2::regs) => transExpressionRegister(expr, r1, r2, regs)
        case (r1::regs) => transExpressionAccumulator(expr, r1, regs)
      }
  }

  // Register machine approach
  private def transExpressionRegister(expr: Expression, reg1: Register, reg2: Register, regs: Seq[Register]): Seq[Instruction] = {
    expr match {
      case BinaryOperatorExpr(e1, binOp, e2) => {
        if (weight(e1) > weight(e2)) {
          // e1 first
          val evalExpr = transExpression(e1, reg1+:reg2+:regs) ++
                         transExpression(e2, reg2+:regs)
          evalExpr ++ transBinaryOperator(reg1, binOp, reg2)
        } else {
          // e2 first
          val evalExpr = transExpression(e2, reg2+:reg1+:regs) ++
                         transExpression(e1, reg1+:regs)
          evalExpr ++ transBinaryOperator(reg1, binOp, reg2)
        }
      }
      case IntegerLiteral(value) => Seq(MOVS(reg1, value))
      case BoolLiteral(value) => val v = if (value) 1 else 0
                                 Seq(MOVS(reg1, v))
      case CharLiteral(value) => Seq(MOVCH(reg1, value))
    }
  }

  // Accumulator machine approach
  private def transExpressionAccumulator(expr: Expression, reg1: Register, regs: Seq[Register]): Seq[Instruction] = {
    Seq()
  }

}
