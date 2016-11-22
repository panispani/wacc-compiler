package wacc.arm

object RETURN extends POP(Seq(PC))
object NEW_STACK_FRAME extends PUSH(Seq(LR))

case class COMMENT(string: String) extends Instruction {
  override val name = s"# $string"
}

object NEWLINE extends Instruction {
  override val name = ""
}
