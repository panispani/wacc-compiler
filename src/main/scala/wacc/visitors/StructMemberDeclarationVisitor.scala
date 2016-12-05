package wacc.visitors

import antlr.WACCParser.{StructMemberDeclarationContext}
import antlr.WACCParserBaseVisitor
import wacc.VariableReference

object StructMemberDeclarationVisitor extends WACCParserBaseVisitor[VariableReference] {
  override def visitStructMemberDeclaration(ctx: StructMemberDeclarationContext): VariableReference  = {
    //TODO: Fix offset
    VariableReference(ctx.IDENT().toString, ctx.`type`.accept(TypeVisitor), 0)
  }
}
