package wacc.visitors

import antlr.WACCParser.ArrayElementContext
import antlr.WACCParserBaseVisitor
import wacc.{SymbolTable, VariableReference}
import wacc.constructs._

import scala.collection.JavaConversions._
import wacc.visitor._

object ArrayElementVisitor extends WACCParserBaseVisitor[Either[CompilationError, ArrayElement]] {

  override def visitArrayElement(ctx: ArrayElementContext): Either[CompilationError, ArrayElement] = {
    val identifier = ctx.variableReference().getText

    SymbolTable.currentTable.lookupAll(identifier) match {
      case Some(VariableReference(ArrayType(elemtype))) => for {
        indexes <- sequence(ctx.expression().toList map (e => e.accept(ExpressionVisitor))).right
      } yield ArrayElement(identifier, indexes)

      case None    => Left(SemanticError("Variable not declared"))
      case default => Left(SemanticError("Identifier is not an array reference"))
    }
  }

}
