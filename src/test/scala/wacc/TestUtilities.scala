package wacc

import antlr.{WACCLexer, WACCParser, WACCParserBaseVisitor}
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream, ParserRuleContext}
import org.scalatest._
import wacc.arm.{Instruction, Label, Registers}
import wacc.codegeneration.{TransFunctions, TransStatements}
import wacc.constructs.{CompilationError, Function, Program, ScopeStatement, Statement}
import wacc.visitors.ProgramVisitor

object TestUtilities {
  def translateWithoutSections(program: Program): Seq[Instruction] = {
    translateWithoutSections(program.functions) ++ translateWithoutSections(program.main)
  }

  def translateWithoutSections(functions: Seq[Function]): Seq[Instruction] = {
    functions flatMap (f => TransFunctions.transFunction(f, Registers.expressionRegs))
  }

  def translateWithoutSections(statement: ScopeStatement): Seq[Instruction] = {
    TransStatements.transStatement(statement, Registers.expressionRegs)
  }

  def buildProgram(inputString: String): Either[Seq[CompilationError], Program] = {
    val parser = setupParser(inputString)
    val tree = parser.program()
    val program = ProgramVisitor.visit(tree)

    program
  }

  def setupParser(code: String): WACCParser = {
    val input = new ANTLRInputStream(code)
    val lexer = new WACCLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new WACCParser(tokens)

    parser.addErrorListener(new SyntaxErrorListener())

    parser
  }

  def buildSubProgram[T](rule: () => ParserRuleContext, visitor: WACCParserBaseVisitor[T]): T = {
    val tree = rule()
    val program = visitor.visit(tree)

    program
  }

  def functionlessProgram(statements: Seq[Statement]): Program = {
    Program(Seq(), ScopeStatement(statements, SymbolTable.globalTable))
  }

  trait SymbolTableState extends BeforeAndAfterEach { this: Suite =>
    override def afterEach(): Unit = {
      try super.afterEach()
      finally SymbolTable.clearAll()
    }
  }

  trait LabelCreationState extends BeforeAndAfterEach { this: Suite =>
    override def afterEach(): Unit = {
      try super.afterEach()
      finally Label.clear()
    }

  }
}
