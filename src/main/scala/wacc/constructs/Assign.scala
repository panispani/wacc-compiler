package wacc.constructs

trait Typed {
  val varType: Type
}

trait AssignValue extends Typed
trait AssignTarget extends Typed



