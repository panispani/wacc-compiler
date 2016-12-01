package wacc.constructs

import wacc.arm.Register
import wacc.codegeneration.CodeSegment

trait Typed {
  val vartype: Type
}

trait AssignValue extends Typed {
  def transAssignRhs(registers: Seq[Register]): CodeSegment
}

trait AssignTarget extends Typed



