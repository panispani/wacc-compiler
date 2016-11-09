package wacc.constructs

case class Literal() extends Expression {
  override val vartype = String
}

case class ArrayLiteral(elements: Seq[Expression]) extends AssignValue {
  val vartype: ArrayType = ArrayType(elements.head.vartype)
}


