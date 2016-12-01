package wacc.codegeneration

import wacc.arm._
import wacc.constructs._

object TransAssignRhs {

  def transAssignRhs(value: AssignValue, registers: Seq[Register]): Seq[Instruction] = {
    value.transAssignRhs(registers).instructions
  }
}
