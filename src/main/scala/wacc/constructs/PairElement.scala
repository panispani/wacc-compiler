package wacc.constructs

case class PairElement(selector: Selector, expression: Expression) extends AssignValue with AssignTarget {
  override val vartype: Type = String
}

case class Selector(selector: String)
object FirstSelector extends Selector("fst")
object SecondSelector extends Selector("snd")
