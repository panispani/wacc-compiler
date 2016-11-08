package wacc.visitors

import antlr.WACCParser.AssignContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.Statement

object StatementVisitor extends WACCParserBaseVisitor[Statement] {
  override def visitAssign(ctx: AssignContext): Statement = {
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

