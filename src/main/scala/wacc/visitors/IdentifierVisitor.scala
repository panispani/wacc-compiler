package wacc.visitors

import antlr.WACCParser.{AssignLhsIdentContext, VariableReferenceContext}
import antlr.WACCParserBaseVisitor
import wacc.{SymbolTable, VariableReference}
import wacc.constructs.{CompilationError, SemanticError}

object IdentifierVisitor extends WACCParserBaseVisitor[String] {

  override def visitAssignLhsIdent(ctx: AssignLhsIdentContext): String = {
    ctx.getText
  }

}
