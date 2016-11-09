package wacc.constructs


abstract class CompilationError(val message: String) {
  def raise(): Unit = {
    println(exitCode + " : " + message)
    System.exit(exitCode)
  }
  val exitCode : Int
}

case class SyntaxError(override val message: String) extends CompilationError(message) {
  override val exitCode: Int = 100
}

case class SemanticError(override val message: String) extends CompilationError(message) {
  override val exitCode: Int = 200
}