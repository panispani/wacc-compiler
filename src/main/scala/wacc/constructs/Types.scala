package wacc.constructs

trait Type {
  val size: Int
  val enumId: Int
  def toAssemblyLabel: String = toString
}

case class PrimitiveType(identifier: String, size: Int, enumId: Int) extends Type {
  override def toString(): String = identifier
}

object AnyType extends Type {
  override val size: Int = 0
  override val enumId: Int = 0
}

object String extends ArrayType(Character)
object Integer extends PrimitiveType("int", 4, 1)
object Character extends PrimitiveType("char", 1, 2)
object Boolean extends PrimitiveType("bool", 1, 3)

case class PairType(firstType: Type, secondType: Type) extends Type {
  override val size: Int = 4
  override val enumId: Int = 4

  override def toString: String = s"pair($firstType, $secondType)"
  override def toAssemblyLabel: String = s"pair_${firstType}_$secondType"
}

case class ArrayType(elemtype: Type) extends Type {
  override val size: Int = 4
  override val enumId: Int = 5

  def typeAt(size: Int): Type = {
    if (size == 1) elemtype
    else {
      val nested @ ArrayType(_) = elemtype
      nested.typeAt(size - 1)
    }
  }

  override def toString: String = s"array($elemtype)"
  override def toAssemblyLabel: String = s"array_$elemtype"
}

case class StructType(identifier: String, members: Seq[(String, Type)]) extends Type {
  override val size: Int = 4
  override val enumId: Int = 7
}
