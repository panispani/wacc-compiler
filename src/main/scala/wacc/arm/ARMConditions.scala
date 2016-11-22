package wacc.arm

trait Condition
object GT extends Condition {
  override def toString = "GT"
}

object GE extends Condition {
  override def toString = "GE"
}

object LT extends Condition {
  override def toString = "LT"
}

object LE extends Condition {
  override def toString = "LE"
}

object EQ extends Condition {
  override def toString = "EQ"
}

object NE extends Condition {
  override def toString = "NE"
}

object ALWAYS extends Condition {
  override def toString = ""
}
