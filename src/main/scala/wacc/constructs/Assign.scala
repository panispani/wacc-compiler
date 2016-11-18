package wacc.constructs

trait Typed {
  val vartype: Type
}

case class MemoryLocation(location: Integer)

case class MemoryObject(typed: Typed, memoryLocation: MemoryLocation = MemoryLocation(-1))

trait AssignValue extends Typed
trait AssignTarget extends Typed



