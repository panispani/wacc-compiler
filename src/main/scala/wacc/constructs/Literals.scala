package wacc.constructs

trait Literal extends Expression

case class IntegerLiteral(value: Int) extends Literal {
  override val varType: Type = Integer
}

case class BoolLiteral(value: Boolean) extends Literal {
  override val varType: Type = Boolean
}

case class CharLiteral(value: String) extends Literal {
  override val varType: Type = Character
}

case class StringLiteral(value: String) extends Literal {
  override val varType: Type = String
}

case class ArrayLiteral(elements: Seq[Expression]) extends AssignValue {
  val varType: ArrayType = ArrayType(if (elements.nonEmpty) elements.head.varType else AnyType)
}

case class PairLiteral() extends Literal {
  override val varType: Type = PairType(AnyType, AnyType)
}

case class StructLiteral(members: Seq[Expression]) extends Literal {
  override val varType: Type = StructType("$$$", members map (member => ("", member.varType)))
}
