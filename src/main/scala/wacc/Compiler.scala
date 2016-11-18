package wacc

import antlr.{WACCLexer, WACCParser}
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}
import wacc.codegeneration.IrCodegenerator
import wacc.visitors.ProgramVisitor

object Compiler extends App {
  val input = new ANTLRInputStream(System.in)
  val lexer = new WACCLexer(input)
  val tokens = new CommonTokenStream(lexer)
  val parser = new WACCParser(tokens)

  parser.addErrorListener(new SyntaxErrorListener())

  val tree = parser.program()
  val program = ProgramVisitor.visit(tree)

  program match {
    case Left(errors :+ last) => errors foreach(_.raise()) ; last.raiseAndExit()
    case Right(program) => {
      val irCodegen = new IrCodegenerator
      val ir = irCodegen.codegen(program)
      //peephole optimisations
    }
  }

}
