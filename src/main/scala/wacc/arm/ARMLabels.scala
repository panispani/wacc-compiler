package wacc.arm

case class Label(name: String)

object Label {
  private var currentLabelNumber: Int = -1

  def apply(): Label = {
    currentLabelNumber += 1
    Label("L" + currentLabelNumber)
  }

  def clear(): Unit = { currentLabelNumber = -1 }
}

case class DefineLabel(label: Label) extends Instruction {
  override val name: String = s"$label:"
}
