package wacc.constructs

trait Literal extends Expression

case class IntegerLiteral(value: Int) extends Literal {
  override val vartype: Type = Integer
}

case class BoolLiteral(value: Boolean) extends Literal {
  override val vartype: Type = Boolean
}

case class CharLiteral(value: Char) extends Literal {
  override val vartype: Type = Character
}

case class StringLiteral(value: String) extends Literal {
  override val vartype: Type = String
}

case class ArrayLiteral(elements: Seq[Expression]) extends AssignValue {
  val vartype: ArrayType = ArrayType(if (elements.nonEmpty) elements.head.vartype else AnyType)
}

case class PairLiteral() extends Literal {
  override val vartype: Type = PairType(AnyType, AnyType)
}
