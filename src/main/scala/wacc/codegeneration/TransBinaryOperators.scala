package wacc

import wacc.constructs._
import wacc.codegeneration._

/**
  * Created by panayiotis on 17/11/16.
  */
package object TransBinaryOperators {

  def transBinaryOperatorReg(r1: Register, binOp: BinaryOperator, r2: Register): Seq[Instruction] = {
    binOp match {
      case BinaryOperator("*")  => Seq(MUL(r1, r1, r2))
      case BinaryOperator("/")  => Seq() // We should implement this
      case BinaryOperator("%")  => Seq()
      case BinaryOperator("+")  => Seq(ADD(r1, r1, RegisterOperand(r2)))
      case BinaryOperator("-")  => Seq(SUB(r1, r1, RegisterOperand(r2)))
      case BinaryOperator(">")  => Seq(CMP(r1, RegisterOperand(r2)), MOV(r1, ImmOperand(1), GT()), MOV(r1, ImmOperand(0)), LE())
      case BinaryOperator(">=") => Seq(CMP(r1, RegisterOperand(r2)), MOV(r1, ImmOperand(1), GE()), MOV(r1, ImmOperand(0)), LT())
      case BinaryOperator("<")  => Seq(CMP(r1, RegisterOperand(r2)), MOV(r1, ImmOperand(1), LT()), MOV(r1, ImmOperand(0)), GE())
      case BinaryOperator("<=") => Seq(CMP(r1, RegisterOperand(r2)), MOV(r1, ImmOperand(1), LE()), MOV(r1, ImmOperand(0)), GT())
      case BinaryOperator("==") => Seq(CMP(r1, RegisterOperand(r2)), MOV(r1, ImmOperand(1), EQ()), MOV(r1, ImmOperand(0)), NE())
      case BinaryOperator("!=") => Seq(CMP(r1, RegisterOperand(r2)), MOV(r1, ImmOperand(1), NE()), MOV(r1, ImmOperand(0)), EQ())
      case BinaryOperator("&&") =>
      case BinaryOperator("||") =>
    }
    Seq()
  }

  def transBinaryOperatorAcc(r1: Register, binOp: BinaryOperator): Seq[Instruction] = {
    // other operand is on top of the stack
    binOp match {
      case BinaryOperator("*")  =>
      case BinaryOperator("/")  =>
      case BinaryOperator("%")  =>
      case BinaryOperator("+")  =>
      case BinaryOperator("-")  =>
      case BinaryOperator(">")  =>
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
