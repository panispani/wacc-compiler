package wacc.constructs

trait Type {
  val size: Int
}

case class PrimitiveType(identifier: String, size: Int) extends Type
case class ArrayType(elemtype: Type) extends Type {
  override val size: Int = 4

  def baseType(): Type = elemtype match {
    case ArrayType(nested @ ArrayType(_)) => nested.baseType()
    case ArrayType(base) => base
  }
}
case class PairType(firstType: Type, secondType: Type) extends Type {
  override val size: Int = 4
}

object String extends ArrayType(Character)
object Integer extends PrimitiveType("int", 4)
object Boolean extends PrimitiveType("bool", 1)
object Character extends PrimitiveType("char", 1)
object AnyType extends Type {
  override val size: Int = 0
}
