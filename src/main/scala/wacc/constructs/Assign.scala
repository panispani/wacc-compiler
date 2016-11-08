package wacc.constructs

trait AssignValue
trait AssignTarget
case class Assign(target: AssignTarget, value: AssignValue)
