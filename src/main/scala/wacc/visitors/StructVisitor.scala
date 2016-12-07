package wacc.visitors

import antlr.WACCParser.StructContext
import antlr.WACCParserBaseVisitor
import wacc.VariableReference
import wacc.constructs.{CompilationError, Struct}

import scala.collection.JavaConversions._

object StructVisitor extends WACCParserBaseVisitor[Either[CompilationError, Struct]] {

  override def visitStruct(ctx: StructContext): Either[CompilationError, Struct] = {
    var offset = 0

    val members = ctx.structMemberDeclaration().toList map (x => {
      val varType = x.`type`.accept(TypeVisitor)
      val vr = VariableReference(x.IDENT().toString, varType, offset)
      offset += varType.size
      vr
    })

    Right(Struct(ctx.IDENT().getText, members))
  }
}
