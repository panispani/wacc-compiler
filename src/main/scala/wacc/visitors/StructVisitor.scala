package wacc.visitors

import antlr.WACCParser.StructContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.{CompilationError, Struct}

import scala.collection.JavaConversions._

object StructVisitor extends WACCParserBaseVisitor[Either[CompilationError, Struct]] {

  override def visitStruct(ctx: StructContext): Either[CompilationError, Struct] = {
    val members = ctx.structMemberDeclaration().toList map (_.accept(StructMemberDeclarationVisitor))

    Right(Struct(ctx.IDENT().getText, members))
  }
}
