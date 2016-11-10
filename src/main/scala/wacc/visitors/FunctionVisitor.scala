package wacc.visitors

import antlr.WACCParser.FunctionContext
import antlr.WACCParserBaseVisitor
import wacc.constructs.{CompilationError, ConditionalStatement, Function, LoopStatement, Param, ReturnStatement, SemanticError, Statement, SyntaxError}
import wacc.visitor._
import wacc.{FunctionReference, SymbolTable, VariableReference}

import scala.collection.JavaConversions._

object FunctionVisitor extends WACCParserBaseVisitor[Either[CompilationError, Function]] {

  override def visitFunction(ctx: FunctionContext): Either[CompilationError, Function] = {
    val params = Option(ctx.parameterList()) match {
      case None => Seq()
      case Some(ls) => ls.parameter().toList
    }

    val name = ctx.IDENT().getText
    val args: Seq[Param] = params map (_.accept(ParamVisitor))
    val returnType = ctx.`type`().accept(TypeVisitor)

    SymbolTable.currentTable.addTyped(name, FunctionReference(returnType, args map (_.variable.vartype)))

    SymbolTable.openScope()

    args map (arg => {
      val ident = arg.variable.identifier

      if (SymbolTable.currentTable.lookup(ident).isDefined) {
        return Left(SemanticError("A function shouldn't have two or more parameters with the same"))
      }

      SymbolTable.currentTable.addTyped(ident, VariableReference(arg.variable.vartype))
    })

    def SyntaxErrorIfNotReturn(stat: Statement): Either[SyntaxError, Statement] = stat match {
        case ReturnStatement(expr) => Right(stat)
        case ConditionalStatement(expr, trueStats, falseStats) => {
          val trueRes = SyntaxErrorIfNotReturn(trueStats.last)
          val falseRes = SyntaxErrorIfNotReturn(falseStats.last)
          trueRes match {
            case Left(x) => Left(x)
            case Right(x) => falseRes
          }
        }
        case LoopStatement(expr, stats) => SyntaxErrorIfNotReturn(stats.last)
        case _ => Left(SyntaxError("The last statement of a function should be a return"))
    }

    val rightProjection = sequence(ctx.sequence().statement().toList map (_.accept(StatementVisitor))).right

//    val res = for {
//      body <- rightProjection flatMap (stats => SyntaxErrorIfNotReturn(stats.last))
//    } yield Function(name, args, returnType, body)

    val res = for {
      body <- sequence(ctx.sequence().statement().toList map (_.accept(StatementVisitor))).right
    } yield Function(name, args, returnType, body)

    SymbolTable.closeScope()

    res
  }
}

// function must have a return statement - syntax error
// return statement is the last statement of the function - semantic error
// return values(s) are the same(type) as the function type - semantic error


//last statement - return
//get list of return types
//not null, all same, same with returnType
//func parameters not duplicated
//funcNotRedefined