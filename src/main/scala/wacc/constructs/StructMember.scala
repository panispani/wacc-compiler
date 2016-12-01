package wacc.constructs

import wacc.VariableReference

case class StructMember(struct: VariableReference, member: String, memberType: Type) extends Expression {
  override val varType: Type = memberType
}
