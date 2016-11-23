package wacc

import wacc.arm._
import wacc.constructs.{LenOp, _}

package object TransUnaryOperators {

  def transUnaryOperator(r: Register, op: UnaryOperator): Seq[Instruction] = {
    op match {
      case LenOp  => Seq(LDR(r, RegisterAddress(r, 0)))
    }
  }

}
