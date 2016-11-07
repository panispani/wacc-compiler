package wacc.constructs

trait BaseType

case class PrimitiveType(identifier: Identifier) extends BaseType
case class ArrayType(elemtype: BaseType) extends BaseType
case class PairType(firstType: BaseType, secondType: BaseType) extends BaseType
case class ErasedPair() extends BaseType

object String extends PrimitiveType(Identifier("string"))
object Integer extends PrimitiveType(Identifier("int"))
object Boolean extends PrimitiveType(Identifier("bool"))
object Character extends PrimitiveType(Identifier("char"))

