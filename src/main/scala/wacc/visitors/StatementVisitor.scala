package wacc.visitors

import antlr.WACCParser.AssignContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.Statement

object StatementVisitor extends WACCParserBaseVisitor[Statement] {
  override def visitAssign(ctx: AssignContext): Statement = {
    Statement()
  }
}
