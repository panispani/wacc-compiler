package wacc

import wacc.constructs._
import wacc.codegeneration._

/**
  * Created by panayiotis on 17/11/16.
  */
package object TransBinaryOperators {

  def transBinaryOperatorReg(r1: Register, binOp: BinaryOperator, r2: Register): Seq[Instruction] = {
    binOp match {
      case BinaryOperator("*") => Seq(MULS(r1, r2))
      case BinaryOperator("/") => Seq() // We should implement this
      case BinaryOperator("%") => Seq()
      case BinaryOperator("+") => Seq(ADD(r1, r1, r2))
      case BinaryOperator("-") => Seq(SUB(r1, r1, r2))
      case BinaryOperator(">") =>
      case BinaryOperator(">=") =>
      case BinaryOperator("<")  =>
      case BinaryOperator("<=") =>
      case BinaryOperator("==") =>
      case BinaryOperator("!=") =>
      case BinaryOperator("&&") =>
      case BinaryOperator("||") =>
    }
    Seq()
  }

  def transBinaryOperatorAcc(r1: Register, binOp: BinaryOperator): Seq[Instruction] = {
    // other operand is on top of the stack
    binOp match {
      case BinaryOperator("*") =>
      case BinaryOperator("/") =>
      case BinaryOperator("%") =>
      case BinaryOperator("+") =>
      case BinaryOperator("-") =>
      case BinaryOperator(">") =>
      case BinaryOperator(">=") =>
      case BinaryOperator("<")  =>
      case BinaryOperator("<=") =>
      case BinaryOperator("==") =>
      case BinaryOperator("!=") =>
      case BinaryOperator("&&") =>
      case BinaryOperator("||") =>
    }
    Seq()
  }

}
