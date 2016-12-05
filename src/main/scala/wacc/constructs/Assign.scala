package wacc.constructs

import wacc.arm.Register
import wacc.codegeneration.CodeSegment

trait Typed {
  val varType: Type
}

trait AssignValue extends Typed {
  def transAssignRhs(registers: Seq[Register]): CodeSegment
}

trait AssignTarget extends Typed



