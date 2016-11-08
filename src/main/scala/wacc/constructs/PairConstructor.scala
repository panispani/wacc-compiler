package wacc.constructs

case class PairConstructor(firstExp: Expression, secondExp: Expression) extends AssignValue {
  override val vartype: Type = PairType(firstExp.vartype, secondExp.vartype)
}
