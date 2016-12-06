package wacc.constructs

import wacc.VariableReference

case class StructMember(struct: VariableReference, memberName: String, memberType: Type) extends Expression {
  override val varType: Type = memberType
}
