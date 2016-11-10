package wacc.visitors

import antlr.WACCParser.ArrayElementContext
import antlr.WACCParserBaseVisitor
import wacc.constructs._
import wacc.visitor._
import wacc.{SymbolTable, VariableReference}

import scala.collection.JavaConversions._

object ArrayElementVisitor extends WACCParserBaseVisitor[Either[CompilationError, ArrayElement]] {

  override def visitArrayElement(ctx: ArrayElementContext): Either[CompilationError, ArrayElement] = {
    val identifier = ctx.variableReference().getText

    SymbolTable.currentTable.lookupAll(identifier) match {
      case Some(reference @ VariableReference(ArrayType(elemtype))) => for {
        indexes <- sequence(ctx.expression().toList map (e => e.accept(ExpressionVisitor))).right
      } yield ArrayElement(reference, indexes)

      case None    => Left(SemanticError("Variable not declared"))
      case default => Left(SemanticError("Identifier is not an array reference"))
    }
  }

}
