package wacc.interactive

import java.io.ByteArrayInputStream

import antlr.{WACCLexer, WACCParser}
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}
import wacc.arm.Registers
import wacc.codegeneration.{CodeSegment, TransFunctions, TransStatements}
import wacc.constructs.{CompilationError, Function, SemanticError, Statement, SyntaxError}
import wacc.visitors.{FunctionVisitor, ProgramVisitor, StatementVisitor}

object ICompiler extends App {

  private def execStatement(tokens: CommonTokenStream): Either[CompilationError, Statement] = {
    val parser = new WACCParser(tokens)
    parser.addErrorListener(new ISyntaxErrorListener())
    val tree = parser.statement()
    try {
      StatementVisitor.visit(tree)
    } catch {
      case _: NullPointerException => Left(SyntaxError("It's not a statement", null))
    }
  }

  private def execFunction(tokens: CommonTokenStream): Either[CompilationError, Function] = {
    val parser = new WACCParser(tokens)
    parser.addErrorListener(new ISyntaxErrorListener())
    val tree = parser.function()
    try {
      ProgramVisitor.defineFunction(tree) match {
        case Some(SemanticError(error, symbol)) =>
          return Left(SemanticError(error, symbol))
        case None => FunctionVisitor.visit(tree)
      }
    } catch {
      case _: NullPointerException => Left(SyntaxError("It's not a function", null))
    }
  }

  while (true) {
    val inputStatement = scala.io.StdIn.readLine("wacc> ")
    val input = new ANTLRInputStream(new ByteArrayInputStream(inputStatement.getBytes()))
    val lexer = new WACCLexer(input)
    val tokens = new CommonTokenStream(lexer)

    execStatement(tokens) match {
      case Right(stmt) =>
        CodeSegment().extend(TransStatements.transStatement(stmt, Registers.expressionRegs)).release()
      case Left(SyntaxError("It's not a statement", null)) =>
        println(tokens.toString)
        execFunction(tokens) match {
          case Right(f) =>
            CodeSegment().extend(TransFunctions.transFunction(f, Registers.expressionRegs)).release()
          case Left(SyntaxError("It's not a function", null)) =>
            ;
          case Left(fError) =>
            fError.raise()
        }
      case Left(error) =>
        error.raise()
    }

  }

}
