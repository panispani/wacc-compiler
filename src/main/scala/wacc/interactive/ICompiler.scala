package wacc.interactive

import java.io.{ByteArrayInputStream, _}
import scala.language.postfixOps
import antlr.{WACCLexer, WACCParser}
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}
import wacc.arm.Registers
import wacc.codegeneration.{CodeSegment, TransFunctions, TransStatements}
import wacc.constructs.{CompilationError, Function, SemanticError, Statement, SyntaxError}
import wacc.visitors.{FunctionVisitor, ProgramVisitor, StatementVisitor}

import scala.sys.process._

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
    //parser.removeErrorListeners() We want error messages by at least one
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


  var code = ""
  while (true) {
    val input = scala.io.StdIn.readLine("\nwacc> ")

        val ASM = new PrintWriter(new File("asm.s" ))
        val inputStmt = new ANTLRInputStream(new ByteArrayInputStream(input.getBytes()))
        val inputFun = new ANTLRInputStream(new ByteArrayInputStream(input.getBytes()))
        val lexerStmt = new WACCLexer(inputStmt)
        val lexerFun = new WACCLexer(inputFun)
        val tokensStmt = new CommonTokenStream(lexerStmt)
        val tokensFun = new CommonTokenStream(lexerFun)

        execStatement(tokensStmt) match {
          case Right(stmt) =>
            var newcode = ""
            CodeSegment().extend(TransStatements.transStatement(stmt, Registers.expressionRegs)).instructions foreach(x => newcode.concat(x.toString))
            code = code + newcode
          case Left(SyntaxError("It's not a statement", null)) =>
            execFunction(tokensFun) match {
              case Right(f) =>
                val newcode = ""
                CodeSegment().extend(TransFunctions.transFunction(f, Registers.expressionRegs)).instructions foreach(x => newcode.concat(x.toString))
                code = newcode + code
              case Left(SyntaxError("It's not a function", null)) =>
                println()
              case Left(fError) =>
                fError.raise()
            }
          case Left(error) =>
            error.raise()
        }

        ASM.write(code)
        ASM.close()

        val assemble = "arm-linux-gnueabi-gcc -o exe -mcpu=arm1176jzf-s -mtune=arm1176jzf-s asm.s" !
        val execute = "qemu-arm -L /usr/arm-linux-gnueabi/ exe" !

  }
}

