package wacc.arm

abstract class DataInstruction extends Instruction {
  val size: Int = 0
}

case class AsciiData(data: String) extends DataInstruction {
  override val size: Int    = data.length - 2 // double quotes
  override val name: String = ".word " + size + "\n.ascii " + data
}
