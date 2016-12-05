package wacc.visitors

import antlr.WACCParser.{StructMemberContext}
import antlr.WACCParserBaseVisitor
import wacc.{SymbolTable, VariableReference}
import wacc.constructs._

import scala.util.Either

object StructMemberVisitor extends WACCParserBaseVisitor[Either[CompilationError, StructMember]] {
  override def visitStructMember(ctx: StructMemberContext): Either[CompilationError, StructMember] = {
    val structIdentifier = ctx.IDENT(0).getText
    val memberIdentifier = ctx.IDENT(1).getText

    SymbolTable().lookupDeep(structIdentifier) match {
      case Some(ref @ VariableReference(x, varType: StructType, offset)) => {
        val memberType = varType.members.find(s => s._1 == memberIdentifier).orNull._2
        Right(StructMember(ref, memberIdentifier, memberType))
      }
      case None    => Left(SemanticError("Variable not declared", ctx.start))
      case _ => Left(SemanticError("Identifier is not a struct reference", ctx.start))
    }
  }
}
