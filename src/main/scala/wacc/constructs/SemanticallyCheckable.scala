package wacc.constructs

trait SemanticallyCheckable {
  def semanticCheck() = System.out.println("checking")

  semanticCheck()
}
