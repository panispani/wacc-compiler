package wacc.constructs

import org.antlr.v4.runtime.Token


trait CompilationError {
  def message: String
  def symbol: Token
  def exitCode : Int

  def raiseAndExit(): Unit = {
    raise()
    System.exit(exitCode)
  }

  def raise() = {
    System.err.println("line " + symbol.getLine + ":" + symbol.getCharPositionInLine + " " + message)
  }
}

case class SyntaxError(message: String, symbol: Token) extends CompilationError {
  override val exitCode: Int = 100

  override def raise(): Unit = {
    super.raise()
    System.err.println("Syntax error")
  }
}

case class SemanticError(message: String, symbol: Token) extends CompilationError {
  override val exitCode: Int = 200

  override def raise(): Unit = {
    super.raise()
    System.err.println("Semantic error")
  }
}