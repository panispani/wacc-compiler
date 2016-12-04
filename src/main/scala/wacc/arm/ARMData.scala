package wacc.arm

abstract class DataInstruction extends Instruction {
  val size: Int = 0
}

case class AsciiData(data: String) extends DataInstruction {
  private val DOUBLEQUOTES: Int = 2
  override val size: Int    = data.length - countEscapedCharacters(data) // - count of escaped chars in data // double quotes
  override val name: String = ".word " + size + "\n.ascii " + data

  /*
   * half of escaped characters are printed except from the 2 quotes
   * let x be the number of escped characters, then (x - 2)/ 2 + 2 characters are not printed
   */
  private def countEscapedCharacters(data: String): Int = {
    val escapedChars: List[Char] = List('\\', '\b', '\f', '\n', '\r', '\t', '\"', '\'')
    data.filter(escapedChars.contains).length / 2 + 1
   }
}
