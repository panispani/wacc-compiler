package wacc.constructs

trait Type

case class PrimitiveType(identifier: Identifier) extends Type
case class ArrayType(elemtype: Type) extends Type
case class PairType(firstType: Type, secondType: Type) extends Type
case class ErasedPair() extends Type

object String extends PrimitiveType(Identifier("string"))
object Integer extends PrimitiveType(Identifier("int"))
object Boolean extends PrimitiveType(Identifier("bool"))
object Character extends PrimitiveType(Identifier("char"))

