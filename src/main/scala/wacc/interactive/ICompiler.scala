package wacc.interactive

import java.io.ByteArrayInputStream
import java.util.Scanner

import antlr.{WACCLexer, WACCParser}
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream, ConsoleErrorListener}
import wacc.arm.Registers
import wacc.codegeneration.{CodeSegment, TransFunctions, TransStatements}
import wacc.constructs.{CompilationError, Function, SemanticError, Statement, SyntaxError}
import wacc.visitors.{FunctionVisitor, ProgramVisitor, StatementVisitor}

object ICompiler extends App {

  private def execStatement(tokens: CommonTokenStream): Either[CompilationError, Statement] = {
    val parser = new WACCParser(tokens)
    //parser.addErrorListener(new ISyntaxErrorListener())
    parser.removeErrorListeners()
    val tree = parser.statement()
    try {
      StatementVisitor.visit(tree)
    } catch {
      case _: NullPointerException =>
        Left(SyntaxError("It's not a statement", null))
    }
  }

  private def execFunction(tokens: CommonTokenStream): Either[CompilationError, Function] = {
    val parser = new WACCParser(tokens)
    //parser.addErrorListener(new ISyntaxErrorListener())
    parser.removeErrorListeners()
    val tree = parser.function()
    try {
      ProgramVisitor.defineFunction(tree) match {
        case Some(SemanticError(error, symbol)) =>
          Left(SemanticError(error, symbol))
        case None => FunctionVisitor.visit(tree)
      }
    } catch {
      case _: NullPointerException =>
        Left(SyntaxError("It's not a function", null))
    }
  }

  private def exec(): Either[CompilationError, Function] = {
    null
  }


  while (true) {
    val ss = new Scanner(System.in).useDelimiter("\n")
    printf("\nwacc> ")
    //val input = scala.io.StdIn.readLine()
    val input = ss.next()
    val inputStmt = new ANTLRInputStream(new ByteArrayInputStream(input.getBytes()))
    val inputFun = new ANTLRInputStream(new ByteArrayInputStream(input.getBytes()))
    val lexerStmt = new WACCLexer(inputStmt)
    val lexerFun = new WACCLexer(inputFun)
    val tokensStmt = new CommonTokenStream(lexerStmt)
    val tokensFun = new CommonTokenStream(lexerFun)

    execStatement(tokensStmt) match {
      case Right(stmt) =>
        CodeSegment().extend(TransStatements.transStatement(stmt, Registers.expressionRegs)).release()
      case Left(SyntaxError("It's not a statement", null)) =>
        execFunction(tokensFun) match {
          case Right(f) =>
            CodeSegment().extend(TransFunctions.transFunction(f, Registers.expressionRegs)).release()
          case Left(SyntaxError("It's not a function", null)) =>
            println()
          case Left(fError) =>
            fError.raise()
        }
      case Left(error) =>
        error.raise()
    }
  }
}
