package wacc.arm

abstract class DataInstruction extends Instruction {
  val size: Int = 0
}

case class AsciiData(data: String) extends DataInstruction {
  override val name: String = ".asciz \"" + data + "\""
  override val size: Int    = 4 * data.length
}
