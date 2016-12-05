package wacc.visitors

import antlr.WACCParser.StructMemberContext
import antlr.WACCParserBaseVisitor
import wacc.VariableReference

object StructMemberVisitor extends WACCParserBaseVisitor[VariableReference] {
  override def visitStructMember(ctx: StructMemberContext): VariableReference  = {
    //TODO: Fix offset
    VariableReference(ctx.IDENT().toString, ctx.`type`.accept(TypeVisitor), 0)
  }
}
