package wacc.constructs

trait Literal extends AssignValue

case class ArrayLiteral(elements: Seq[Expression]) extends Literal {
  override val vartype: ArrayType = ArrayType(elements.head.vartype)
}


