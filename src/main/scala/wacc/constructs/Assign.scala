package wacc.constructs

trait Typed {
  val vartype: Type
}

trait AssignValue extends Typed
trait AssignTarget extends Typed



