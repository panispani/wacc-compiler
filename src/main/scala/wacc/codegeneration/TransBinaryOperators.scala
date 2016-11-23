package wacc

import wacc.arm._
import wacc.constructs._

package object TransBinaryOperators {

  def transBinaryOperator(r1: Register, binOp: BinaryOperator, r2: Register): Seq[Instruction] = {
    binOp match {
      case BinaryOperator("*")  => Seq(MUL(r1, r1, r2))

      case BinaryOperator("/")  => Seq(
        MOV(R0, r1),
        MOV(R1, r2),
        BL(StaticCode.checkDivideByZeroLabel),
        BL(StaticCode.divisionLabel),
        MOV(r1, R1)
      )

      case BinaryOperator("%")  => Seq(
        MOV(R0, r1),
        MOV(R1, r2),
        BL(StaticCode.checkDivideByZeroLabel),
        BL(StaticCode.moduleLabel)
      )

      case BinaryOperator("+")  => Seq(ADD(r1, r1, r2))

      case BinaryOperator("-")  => Seq(SUB(r1, r1, r2))

      case BinaryOperator(">")  => Seq(CMP(r1, r2), MOV(r1, ImmOperand(1), GT), MOV(r1, ImmOperand(0), LE))

      case BinaryOperator(">=") => Seq(CMP(r1, r2), MOV(r1, ImmOperand(1), GE), MOV(r1, ImmOperand(0), LT))

      case BinaryOperator("<")  => Seq(CMP(r1, r2), MOV(r1, ImmOperand(1), LT), MOV(r1, ImmOperand(0), GE))

      case BinaryOperator("<=") => Seq(CMP(r1, r2), MOV(r1, ImmOperand(1), LE), MOV(r1, ImmOperand(0), GT))

      case BinaryOperator("==") => Seq(CMP(r1, r2), MOV(r1, ImmOperand(1), EQ), MOV(r1, ImmOperand(0), NE))

      case BinaryOperator("!=") => Seq(CMP(r1, r2), MOV(r1, ImmOperand(1), NE), MOV(r1, ImmOperand(0), EQ))

      case BinaryOperator("&&") => Seq(AND(r1, r1, r2))

      case BinaryOperator("||") => Seq(ORR(r1, r1, r2))
    }
  }

}
