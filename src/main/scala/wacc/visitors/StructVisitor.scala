package wacc.visitors

import antlr.WACCParser.StructContext
import antlr.WACCParserBaseVisitor
import wacc.{SymbolTable, VariableReference}
import wacc.constructs.{CompilationError, SemanticError, Struct}

import scala.collection.JavaConversions._

object StructVisitor extends WACCParserBaseVisitor[Either[CompilationError, Struct]] {

  override def visitStruct(ctx: StructContext): Either[CompilationError, Struct] = {
    val name = ctx.IDENT().getText

    // Check for duplicate struct name
    if (SymbolTable.structsTable contains name)
      return Left (SemanticError("Attempted redefinition of struct " + name, ctx.start))

    var offset = 0

    val members = ctx.structMemberDeclaration().toList map (x => {
      val varType = x.`type`.accept(TypeVisitor)
      val vr = VariableReference(x.IDENT().toString, varType, offset)
      offset += varType.size
      vr
    })

    val struct = Struct(ctx.IDENT().getText, members)
    SymbolTable.declareStruct(struct)

    Right(struct)
  }
}
