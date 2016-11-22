package wacc.arm

case class ARMSection(sectionName: String) extends Instruction {
  override val name: String = s".$sectionName\n"
}
