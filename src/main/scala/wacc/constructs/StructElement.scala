package wacc.constructs

import wacc.VariableReference

case class StructElement(struct: VariableReference, member: String, memberType: Type) extends Expression {
  override val vartype: Type = memberType
}
