package wacc.constructs

trait Typed {
  val vartype: Type
}

trait AssignValue extends Typed
trait AssignTarget extends Typed

case class Declare(vartype: Type, identifier: String, value: AssignValue) extends Statement {
}

