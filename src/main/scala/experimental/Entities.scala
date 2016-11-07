package experimental

import scala.collection.immutable.List

trait Type {}
object Integer extends Type
object Bool extends Type
object StringType extends Type
object CharType extends Type
object Pair extends Type
case class Array(elementType: Type) extends Type


trait Ident
class Node
case class StatementNode() extends Node
case class ParamNode(paramType: Type, name: Ident) extends Node
case class FunctionNode(retType: Type, fname: Ident, paramList: List[ParamNode], stmt: StatementNode) extends Node
case class ProgramNode(functions: List[FunctionNode], stmt: StatementNode) extends Node


trait Expression {
  def valueType() : Type
  def semanticCheck() : Boolean = true
}
case class IntegerLiteral(value: Int) extends Expression {
  override def valueType(): Type = Integer
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

case class StringLiteral(value: String) extends Expression {
  override def valueType(): Type = StringType
}

case class CharLiteral(value: Char) extends Expression {
  override def valueType(): Type = CharType
}

case class PairLiteral(value: String) extends Expression {
  override def valueType(): Type = Pair
}




