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
  * Created by panayiotis on 17/11/16.
  */
package object transBinaryOperators {

  def transBinaryOperator(r1: Register, binOp: BinaryOperator, r2: Register): Seq[Instruction] = {
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
