package wacc

import wacc.arm._
import wacc.constructs.{LenOp, _}

package object TransUnaryOperators {

  def transUnaryOperator(r: Register, op: UnaryOperator): Seq[Instruction] = {
    op match {
      case LenOp  => Seq(LDR(r, RegisterAddress(r, 0)))

      case MinusOp => Seq(RSBS(r, r, ImmOperand(0)))

      case NotOp => Seq(EOR(r, r, ImmOperand(1)))
    }
  }

}
