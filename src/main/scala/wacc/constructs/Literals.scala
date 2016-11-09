package wacc.constructs

trait Literal extends Expression

case class IntegerLiteral(value: Integer) extends Literal {
  override val vartype: Type = Integer
}

case class ArrayLiteral(elements: Seq[Expression]) extends AssignValue {
  val vartype: ArrayType = ArrayType(elements.head.vartype)
}


