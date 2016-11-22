package wacc.arm


case class GLOBAL(label: String) extends Instruction {
  override val name: String = s".global $label"
}

