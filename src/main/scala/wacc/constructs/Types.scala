package wacc.constructs

trait Type

case class PrimitiveType(identifier: String) extends Type
case class ArrayType(elemtype: Type) extends Type
case class PairType(firstType: Type, secondType: Type) extends Type
case class ErasedPair() extends Type

object String extends PrimitiveType("string")
object Integer extends PrimitiveType("int")
object Boolean extends PrimitiveType("bool")
object Character extends PrimitiveType("char")
