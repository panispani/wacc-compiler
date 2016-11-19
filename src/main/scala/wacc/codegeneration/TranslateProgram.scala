package wacc

import wacc.TransFunctions._
import wacc.TransStatements._
import wacc.codegeneration._
import wacc.constructs._
/**
  * Created by panayiotis on 16/11/16.
  */
package object codegeneration {
  implicit class ProgramTranslate(program: Program) {
    def translate = program.functions.flatMap(_.translate) ++ program.statements.flatMap(_.translate)
  }

  implicit class FunctionTranslate(function: Function) {
    def translate = Seq() ++ function.stmt.flatMap(_.translate)
  }

  implicit class StatementTranslate(statement: Statement) {
    def translate = statement match {
      case ExitStatement(e) => Seq()
      case any => Seq()
    }
  }
  def transProgram(functions: Seq[Function], stmts: Seq[Statement], registers: Seq[Register]): Seq[Instruction] = {
    val functionInstructions =
      for {
        f <- functions
      } yield transNext(f, registers)

    val mainInstructions = transStatementSequence(stmts, registers)

    val stackBytes = VarLog.byteCount()
    // add labels later
    functionInstructions.flatten ++
      Seq(SUB(SP, SP, ImmOperand(stackBytes))) ++
      mainInstructions ++
      Seq(ADD(SP, SP, ImmOperand(stackBytes)), MOV(R0, ImmOperand(0)))
  }

  def transNext(a: Any, registers: Seq[Register]): Seq[Instruction] = {
    a match {
      case Program(functions, stmt)
      => {
//        transProgram(functions, stmt, registers)
        val functionDefinitions = functions flatMap(transNext(_, registers))
        val statementInstructions = stmt flatMap(transNext(_, registers))
        functionDefinitions ++ statementInstructions
      }
      case Function(ident, params, vartype, stmt)
      => transFunction(ident, params, vartype, stmt, registers)
      case stmt:Statement
      => transStatement(stmt, registers)
      case default
      => println("can we even reach this point"); Seq[Instruction]()
    }
  }

  def visit[T](ast: Any, visitor: (Any) => Seq[T]): Seq[T] = ast match {
    case Program(functions, statements) => visitor(functions) ++ visitor(statements)
    case Seq(xs @ _ *) => xs.flatMap(visitor)
    case Function(_, _, _, body) => visitor(body)
    case s: Statement => visitor(s)
  }

  def visitProgram(p: Program): Seq[Instruction] = {
    visit(p, functionVisitor) ++ visit(p, statementVisitor)
  }

  def functionVisitor(ast: Any): Seq[Instruction] = ast match {
    case Function(_, _, _, _) => Seq() // do some magic;
    case a => visit(a, functionVisitor)
  }
}
