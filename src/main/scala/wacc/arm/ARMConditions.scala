package wacc.arm

case class Condition() {
  override def toString: String = this.getClass.getSimpleName.init
}

object GT extends Condition

object GE extends Condition

object LT extends Condition

object LE extends Condition

object EQ extends Condition

object NE extends Condition

//todo: Hack
object CS extends Condition

object ALWAYS extends Condition {
  override def toString = ""
}
