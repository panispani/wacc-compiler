package wacc.arm

abstract class DataInstruction extends Instruction {
  val size: Int = 0
}

case class AsciiData(labelAddress: LabelAddress, data: String) extends DataInstruction {
  override val name: String = s".asciz $data"
  override val size: Int    = 4 * data.length
}
