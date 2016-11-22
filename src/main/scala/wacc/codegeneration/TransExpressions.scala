package wacc

import wacc.TransBinaryOperators._
import wacc.codegeneration.Weight._
import wacc.codegeneration._
import wacc.arm._
import wacc.constructs._

/**
  * Created by panayiotis on 16/11/16.
  */
package object TransExpressions {
  def transExpression(expr: Expression, registers: Seq[Register]): Seq[Instruction] = {
      registers match {
        case (r1::r2::regs) => transExpressionReg(expr, r1, r2, regs)
        case (r1::regs) => transExpressionAcc(expr, r1, regs)
      }
  }

  // Register machine approach
  private def transExpressionReg(expr: Expression, reg1: Register, reg2: Register, regs: Seq[Register]): Seq[Instruction] = {
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
      case IntegerLiteral(value) => Seq(MOV(reg1, ImmOperand(value)))
      case BoolLiteral(value) => val v = if (value) 1 else 0
                                 Seq(MOV(reg1, ImmOperand(v)))
      case CharLiteral(value) => Seq(MOV(reg1, CharOperand(value)))

      case VariableReferenceExpression(_, _) => println("Not implemented"); Seq() //Todo: Implement (should VariableReferenceExpression contain VariableReference?)
    }
  }

  // Accumulator machine approach
  private def getAnotherRegister(reg: Register) = {
    if (reg == R0)
      R1
    else
      R0
  }

  private def transExpressionAcc(expr: Expression, reg1: Register, regs: Seq[Register]): Seq[Instruction] = {
    val reg2 = getAnotherRegister(reg1)
    Seq(PUSH(Seq(reg2))) ++
    transExpressionReg(expr, reg1, reg2, regs) ++
    Seq(POP(Seq(reg2)))
  }
}
