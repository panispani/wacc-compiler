package wacc.constructs

abstract class PairElement(selector: Selector, identifier: String) extends AssignValue {
  override val vartype: Type = String
}

case class Selector(selector: String)
object FirstSelector extends Selector("fst")
object SecondSelector extends Selector("snd")
