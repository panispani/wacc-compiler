package wacc.constructs


abstract class CompilationError(val message: String) {
  def raiseAndExit(): Unit = {
    raise()
    System.exit(exitCode)
  }

  val exitCode : Int

  def raise() = {
    println(exitCode + " : " + message)
  }
}

case class SyntaxError(override val message: String) extends CompilationError(message) {
  override val exitCode: Int = 100
}

case class SemanticError(override val message: String) extends CompilationError(message) {
  override val exitCode: Int = 200
}