package wacc.constructs

case class PairElement(selector: Selector, expression: Expression, varType: Type) extends AssignValue with AssignTarget

case class Selector(selector: String)
object FirstSelector extends Selector("fst")
object SecondSelector extends Selector("snd")
