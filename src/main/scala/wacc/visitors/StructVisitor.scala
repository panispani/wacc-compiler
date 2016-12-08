package wacc.visitors

import antlr.WACCParser.{FunctionContext, StructContext}
import antlr.WACCParserBaseVisitor
import wacc.constructs.{CompilationError, SemanticError, StructType}
import wacc.{SymbolTable, VariableReference}

import scala.collection.JavaConversions._

object StructVisitor extends WACCParserBaseVisitor[Either[CompilationError, (StructType, Seq[FunctionContext])]] {

  override def visitStruct(ctx: StructContext): Either[CompilationError, (StructType, Seq[FunctionContext])] = {
    val name = ctx.name.getText

    // Check for duplicate struct name
    if (SymbolTable.structsTable contains name) {
      return Left(SemanticError("Attempted redefinition of struct " + name, ctx.start))
    }

    val parentName = Option(ctx.parent).map(_.getText)
    val inheritedMembers = parentName.map(SymbolTable.structsTable(_).members).getOrElse(Seq())

    // New members are placed after inherited ones so that we can support polymorphism
    var offset = if (inheritedMembers.nonEmpty) {
      inheritedMembers.last.offset + inheritedMembers.last.varType.size
    } else 0

    val members = ctx.structMemberDeclaration().toList map (x => {
      val varType = x.`type`.accept(TypeVisitor)
      val vr = VariableReference(x.IDENT().toString, varType, offset)

      offset += varType.size
      vr
    })

    val membersName = members.map(_.name)

    //Semantic error if two members have the same name
    if (membersName.distinct.size != membersName.size) {
      return Left (SemanticError("Duplicate members name in struct " + name, ctx.start))
    }

    /** TODO: add checks for first argument being of the instance type if needed
      * otherwise any method will be allowed (could be treated as a static method)
      */

    /** Note that it is ok for methods to be declared in the main function table because
      * because the full function identifier will contain the class type when it is the
      * first argument. For static methods there could be a clash when two classes define
      * a static method of the same name and same arguments. IMO this should be handled by
      * the package/imports system and not by class definition rules.*/

    val struct = StructType(name, inheritedMembers ++ members, parentName)
    SymbolTable.declareStruct(struct)
    Right((struct, ctx.function().toList))
  }
}
