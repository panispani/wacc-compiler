package wacc.constructs

trait Literal extends Expression

case class IntegerLiteral(value: Integer) extends Literal {
  override val vartype: Type = Integer
}

case class ArrayLiteral(elements: Seq[Expression]) extends AssignValue {
  val vartype: ArrayType = ArrayType(elements.head.vartype)
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

case class PairLiteral(firstType: Type, secondType: Type, pair: Option[(Type, Type)]) extends Literal {
  override val vartype: Type = PairType(firstType, secondType)
}
