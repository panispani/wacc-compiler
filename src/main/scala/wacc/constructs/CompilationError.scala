package wacc.constructs


abstract class CompilationError(val message: String) {
  def raise(): Unit = println(message)
}

case class SyntaxError(override val message: String) extends CompilationError(message) {
  override def raise(): Unit = super.raise(); System.exit(100)
}

case class SemanticError(override val message: String) extends CompilationError(message) {
  override def raise(): Unit = super.raise(); System.exit(200)
}