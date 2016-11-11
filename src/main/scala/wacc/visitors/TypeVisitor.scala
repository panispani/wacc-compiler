package wacc.visitors

import antlr.WACCParser.{ArrayTypeContext, ErasedPairContext, PairTypeContext, PrimitiveTypeContext}
import antlr.WACCParserBaseVisitor
import wacc.constructs._

object TypeVisitor extends WACCParserBaseVisitor[Type] {
  override def visitPrimitiveType(ctx: PrimitiveTypeContext): Type =
    if (ctx.getText == "string") String
    else PrimitiveType(ctx.getText)

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
}
