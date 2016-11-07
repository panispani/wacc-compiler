package wacc.visitors

import antlr.WACCParser.{ArrayTypeContext, ErasedPairContext, PairTypeContext, PrimitiveTypeContext}
import antlr.WACCParserBaseVisitor
import wacc.constructs._

object TypeVisitor extends WACCParserBaseVisitor[BaseType] {
  override def visitPrimitiveType(ctx: PrimitiveTypeContext): BaseType =
    PrimitiveType(Identifier(ctx.toString))

  override def visitPairType(ctx: PairTypeContext): BaseType = {
    val firstType = ctx.pairElementType(0).accept(TypeVisitor)
    val secondType = ctx.pairElementType(0).accept(TypeVisitor)
    PairType(firstType, secondType)
  }

  //TODO: Make this functional
  override def visitArrayType(ctx: ArrayTypeContext): BaseType = {
    val elemtype = ctx.notNestedArrayType().accept(TypeVisitor)
    var arrayType: ArrayType = ArrayType(elemtype)

    for (i <- 1 to ctx.RB().size()) {
      arrayType = ArrayType(arrayType)
    }

    arrayType
  }

  override def visitErasedPair(ctx: ErasedPairContext): BaseType = {
    ErasedPair()
  }
}
