package wacc

import java.io.ByteArrayInputStream

import antlr.{WACCLexer, WACCParser}
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}
import arm.Registers
import codegeneration.{CodeSegment, TransFunctions, TransProgram, TransStatements}
import constructs._
import visitors.{FunctionVisitor, ProgramVisitor, StatementVisitor}

object ICompiler extends App {

  private def execStatement(tokens: CommonTokenStream): Either[CompilationError, Statement] = {
    val parser = new WACCParser(tokens)
    parser.addErrorListener(new ISyntaxErrorListener())
    val tree = parser.statement()
    try {
      StatementVisitor.visit(tree)
    } catch {
      case _: Any => Left(SyntaxError("It's not a statement", null))
    }
  }

  private def execFunction(tokens: CommonTokenStream): Either[CompilationError, Function] = {
    val parser = new WACCParser(tokens)
    parser.addErrorListener(new ISyntaxErrorListener())
    val tree = parser.function()
    try {
      FunctionVisitor.visit(tree)
    } catch {
      case _: Any => Left(SyntaxError("It's not a function", null))
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
        execFunction(tokens) match {
          case Right(f) =>
            CodeSegment().extend(TransFunctions.transFunction(f, Registers.expressionRegs)).release()
          case Left(SyntaxError("It's not a function", null)) =>
            SyntaxError("Syntax error", tokens.get(0)).raise()
          case Left(fError) =>
            fError.raise()
        }
      case Left(error) =>
        error.raise()
    }

  }

}
