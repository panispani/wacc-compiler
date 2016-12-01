package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.constructs._
import scala.collection.JavaConversions._

object TypeVisitor extends WACCParserBaseVisitor[Type] {
  override def visitPrimitiveType(ctx: PrimitiveTypeContext): Type =
    ctx.getText match {
      case "int" => Integer
      case "bool" => Boolean
      case "char" => Character
      case "string" => String
    }

  override def visitPairType(ctx: PairTypeContext): Type = {
    val firstType = ctx.firstType.accept(TypeVisitor)
    val secondType = ctx.secondType.accept(TypeVisitor)
    PairType(firstType, secondType)
  }

  override def visitArrayType(ctx: ArrayTypeContext): Type = {
    val elemtype = ctx.notNestedArrayType().accept(TypeVisitor)
    var arrayType = ArrayType(elemtype)

    for (i <- 2 to ctx.RB().size()) {
      arrayType = ArrayType(arrayType)
    }

    arrayType
  }

  override def visitErasedPair(ctx: ErasedPairContext): Type = {
    PairType(AnyType, AnyType)
  }

  override def visitStruct(ctx: StructContext): Type = {
    val members = ctx.structMember().toList map (_.accept(StructMemberVisitor))

    StructType(ctx.IDENT().getText, members map (m => (m.name, m.varType)))
  }
}
