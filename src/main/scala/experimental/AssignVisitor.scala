package experimental

import antlr.WACCParser.{ProgramContext}
import antlr.WACCParserBaseVisitor
import scala.collection.JavaConversions._


/**
  * Created by ema on 07/11/2016.
  */

object ProgramVisitor extends WACCParserBaseVisitor[Program] {

  override def visitProgram(ctx: ProgramContext): Program = {
    val funs = ctx.function().toList map (f => f.accept(FunctionVisitor))
    val stat = ctx.statement().accept(StatementVisitor)

    Program(funs, stat)
  }
}

case class Program(seq: Seq[Function], stat: Statement) {

}

object FunctionVisitor extends WACCParserBaseVisitor[Function]

object StatementVisitor extends WACCParserBaseVisitor[Statement]

trait Statement

trait Function


//class AssignVisitor extends WACCParserBaseVisitor[Assign] {
//
//  override def visitAssign(ctx: AssignContext): Assign = {
//    val lhs = this.visit(ctx.assignLhs())
//    val rhs = this.visit(ctx.assignRhs())
//
//    Assign(lhs, rhs)
//  }
//}
//
//class AssignLhsVisitor extends WACCParserBaseVisitor[AssignLhs] {
//
//  override def visitAssignLhs(ctx: AssignLhsContext): AssignLhs = {
//
//    Assign(lhs, rhs)
//  }
//}
//
//case class Assign(lhs: assignLhs, rhs: assignRhs) {
//
//}
//
//trait assignLhs
//
//trait assignRhs
