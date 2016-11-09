package wacc.visitors

import antlr.WACCParser.ArrayElementContext
import antlr.WACCParserBaseVisitor
import wacc.SymbolTable
import wacc.constructs._

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}
import wacc.Util._

object ArrayElementVisitor extends WACCParserBaseVisitor[Either[CompilationError, ArrayElement]] {

  override def visitArrayElement(ctx: ArrayElementContext): Either[CompilationError, ArrayElement] = {
    val identifier = ctx.IDENT().toString

    SymbolTable.currentTable.lookupAll(identifier) match {
      case Some(variable: Variable) => for {
        indexes <- sequence(ctx.expression().toList map (e => e.accept(ExpressionVisitor))).right
      } yield ArrayElement(identifier, indexes)

      case None    => Left(SemanticError("Variable not declared"))
      case default => Left(SemanticError("Identifier is not an array reference"))
    }
  }

}
