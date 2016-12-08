package wacc.constructs

import wacc.VariableReference

trait Type {
  val size: Int
  def toAssemblyLabel: String = toString
}

case class PrimitiveType(identifier: String, size: Int) extends Type {
  override def toString(): String = identifier
}

case class ArrayType(elemtype: Type) extends Type {
  override val size: Int = 4

  def typeAt(size: Int): Type = {
    if (size == 1) elemtype
    else {
      val nested @ ArrayType(_) = elemtype
      nested.typeAt(size - 1)
    }
  }

  override def toString(): String = s"array($elemtype)"
  override def toAssemblyLabel(): String = s"array_$elemtype"
}
case class PairType(firstType: Type, secondType: Type) extends Type {
  override val size: Int = 4
}
//case class StructType(identifier: String, members: Seq[(String, Type)]) extends Type {
//  override val size: Int = 4
//  override def toString(): String = identifier
//}

case class StructType(identifier: String, members: Seq[VariableReference], parentName: Option[String] = None) extends Type {
  override val size: Int = 4
}

object String extends ArrayType(Character)
object Integer extends PrimitiveType("int", 4)
object Boolean extends PrimitiveType("bool", 1)
object Character extends PrimitiveType("char", 1)

object AnyType extends Type {
  override val size: Int = 0
}
