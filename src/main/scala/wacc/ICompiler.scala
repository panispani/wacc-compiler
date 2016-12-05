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
    if (!tree.isEmpty)
      StatementVisitor.visit(tree)
    else
      Left(SyntaxError("It's not a statement", tree.start))
  }

  private def execFunction(tokens: CommonTokenStream): Either[CompilationError, Function] = {
    val parser = new WACCParser(tokens)
    parser.addErrorListener(new ISyntaxErrorListener())
    val tree = parser.function()
    if (!tree.isEmpty) {
      FunctionVisitor.visit(tree)
    }
    else
      Left(SyntaxError("It's not a function", tree.start))
  }

  // make it loop for statements and functions
  while (true) {
    val inputStatement = scala.io.StdIn.readLine()
    val input = new ANTLRInputStream(new ByteArrayInputStream(inputStatement.getBytes()))
    val lexer = new WACCLexer(input)
    val tokens = new CommonTokenStream(lexer)

    execStatement(tokens) match {
      case Right(stmt) => {
        CodeSegment().extend(TransStatements.transStatement(stmt, Registers.expressionRegs)).release()
      }
      case Left(error) => println("here"); println(tokens)
        execFunction(tokens) match {
          case Right(f) => println("asd"); println(f); CodeSegment().extend(TransFunctions.transFunction(f, Registers.expressionRegs)).release()
          case Left(fError) => println("123");//fError.raise() ; error.raise() // TODO: only one of the two is needed
        }
    }

  }

}
