package wacc

import wacc.arm._
import wacc.constructs._

package object TransBinaryOperators {

  def transBinaryOperator(r1: Register, binOp: BinaryOperator, r2: Register): Seq[Instruction] = {
    binOp match {
      case TimesBinOp  => Seq(MUL(r1, r1, r2))

      case DivBinOp  => Seq(
        MOV(R0, r1),
        MOV(R1, r2),
        BL(StaticCode.checkDivideByZeroLabel),
        BL(StaticCode.divisionLabel),
        MOV(r1, R1)
      )

      case ModBinOp  => Seq(
        MOV(R0, r1),
        MOV(R1, r2),
        BL(StaticCode.checkDivideByZeroLabel),
        BL(StaticCode.moduleLabel),
        MOV(r1, R1)
      )

      case PlusBinOp  => Seq(ADD(r1, r1, r2))

      case MinusBinOp  => Seq(SUB(r1, r1, r2))

      case GtBinOp  => Seq(CMP(r1, r2), MOV(r1, ImmOperand(1), GT), MOV(r1, ImmOperand(0), LE))

      case GteBinOp => Seq(CMP(r1, r2), MOV(r1, ImmOperand(1), GE), MOV(r1, ImmOperand(0), LT))

      case LtBinOp  => Seq(CMP(r1, r2), MOV(r1, ImmOperand(1), LT), MOV(r1, ImmOperand(0), GE))

      case LteBinOp => Seq(CMP(r1, r2), MOV(r1, ImmOperand(1), LE), MOV(r1, ImmOperand(0), GT))

      case EqualsBinOp => Seq(CMP(r1, r2), MOV(r1, ImmOperand(1), EQ), MOV(r1, ImmOperand(0), NE))

      case NequalsBinOp => Seq(CMP(r1, r2), MOV(r1, ImmOperand(1), NE), MOV(r1, ImmOperand(0), EQ))

      case AndBinOp => Seq(AND(r1, r1, r2))

      case OrBinOp => Seq(ORR(r1, r1, r2))
    }
  }

}
