package wacc.visitors

import antlr.WACCParser.AssignLhsIdentContext
import antlr.WACCParserBaseVisitor

object IdentifierVisitor extends WACCParserBaseVisitor[String] {

  override def visitAssignLhsIdent(ctx: AssignLhsIdentContext): String = {
    ctx.variableReference().getText
  }

}
