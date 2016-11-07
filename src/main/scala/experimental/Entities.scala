package experimental

trait Type {}
object Integer extends Type
object Bool extends Type
case class Array(elementType: Type) extends Type

trait Expression {
  def valueType() : Type
  def semanticCheck() : Boolean = true
}
case class IntegerLiteral(value: Int) extends Expression {
  override def valueType(): Type = Integer
  override def semanticCheck(): Boolean =
    Int.MinValue <= value && value <= Int.MaxValue
}

case class BoolLiteral(value: Boolean) extends Expression {
  override def valueType(): Type = Bool
}

case class ArrayLiteral(elements: Seq[Expression]) extends Expression {
  override def semanticCheck(): Boolean
  = elements.isEmpty ||
    (elements forall (_.valueType() equals elements.head.valueType()))

  override def valueType(): Type = Array(elements.head.valueType())
}
