package experimental

import scala.collection.JavaConversions._

import antlr.WACCParser.{ArrayLiteralContext, BoolLiteralContext}
import antlr.{WACCParser, WACCParserBaseVisitor}

class ExpressionVisitor() extends WACCParserBaseVisitor[Expression] {

  override def visitIntLiteral(ctx: WACCParser.IntLiteralContext) = {
    IntegerLiteral(ctx.getText.toInt)
  }

  override def visitBoolLiteral(ctx: BoolLiteralContext): Expression
    = BoolLiteral(ctx.getText.toBoolean)

  override def visitArrayLiteral(ctx: ArrayLiteralContext): Expression
    = ArrayLiteral(ctx.expression().toList map this.visitExpression)
}

trait Type {}
object Integer extends Type
object Bool extends Type
case class Array(elementType: Type) extends Type

trait Expression {
  def valueType() : Type
  def semanticCheck() : Boolean = true
}
case class IntegerLiteral(value: Int) extends Expression {
  override def valueType(): Type = Integer
  override def semanticCheck(): Boolean =
    Int.MinValue <= value && value <= Int.MaxValue
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
