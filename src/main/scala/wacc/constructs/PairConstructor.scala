package wacc.constructs

case class PairConstructor(firstExp: Expression, secondExp: Expression) extends AssignValue {
  override val varType: Type = PairType(firstExp.varType, secondExp.varType)
}
