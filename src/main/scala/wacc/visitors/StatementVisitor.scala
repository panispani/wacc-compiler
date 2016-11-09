package wacc.visitors

import antlr.WACCParser.{ActionContext, AssignContext, DeclareContext, ExpressionActionContext}
import antlr.WACCParserBaseVisitor
import wacc.constructs.Statement

object StatementVisitor extends WACCParserBaseVisitor[Statement] {


  override def visitDeclare(ctx: DeclareContext): Statement = {
    println("kori korou")
    ctx.assignRhs().accept(AssignRhsVisitor)
    Statement()
  }

  override def visitAssign(ctx: AssignContext): Statement = {
    println("kori korou1")
    ctx.assignRhs().accept(AssignRhsVisitor)
    ctx.assignLhs().accept(AssignLhsVisitor)
    Statement()
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

