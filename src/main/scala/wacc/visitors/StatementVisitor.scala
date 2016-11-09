package wacc.visitors

import antlr.WACCParser._
import antlr.WACCParserBaseVisitor
import wacc.constructs.{Declare, PrimitiveType, Skip, Statement}

import scala.util.{Success, Try}

object StatementVisitor extends WACCParserBaseVisitor[Statement] {

  override def visitSkip(ctx: SkipContext): Skip = {
    Skip()
  }

  override def visitDeclare(ctx: DeclareContext): Declare = {
    val vartype = ctx.`type`().accept(TypeVisitor)
    val identifier = ctx.IDENT().toString
    val rhs = ctx.assignRhs().accept(AssignRhsVisitor)

    Declare(vartype, identifier, rhs)
  }

}

/*
        # Skip
          | type IDENT ASSIGN assignRhs                        # Declare
          | assignLhs ASSIGN assignRhs                         # Assign
          | READ assignLhs                                     # Read
          | expressionAction expression                        # Action
          | IF expression THEN statement ELSE statement FI     # Conditional
          | WHILE expression DO statement DONE                 # Loop
          | BEGIN statement END                                # Scope
          | statement SEMICOLON statement                      # Sequence
          */

