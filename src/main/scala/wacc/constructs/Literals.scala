package wacc.constructs

case class Literal(vartype: Type) extends AssignValue

case class ArrayLiteral(elements: Seq[Expression]) extends AssignValue{
  val vartype: ArrayType = ArrayType(elements.head.vartype)
}


