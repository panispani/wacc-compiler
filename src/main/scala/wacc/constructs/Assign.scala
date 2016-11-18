package wacc.constructs

trait Typed {
  val vartype: Type
}

case class MemoryObject(typed: Typed, offset: Int = -1)

trait AssignValue extends Typed
trait AssignTarget extends Typed



