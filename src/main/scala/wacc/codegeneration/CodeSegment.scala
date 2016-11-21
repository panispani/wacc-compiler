package wacc.codegeneration

case class CodeSegment private(instructions: Seq[Instruction]) {

  def this() {
    this(Vector())
  }

  def append(instruction: Instruction) = {
    CodeSegment(instructions :+ instruction)
  }

  def extend(extension: Seq[Instruction]) = {
    CodeSegment(instructions ++ extension)
  }

  /**
    * The consumer will be a function composed of several stages
    * consumer = optimisation1 andThen ... andThen optimisationN andThen print
    *
    * See the companion object below for the default implementation
    * */
  def release()(implicit consumer: (CodeSegment => Unit)) = {
    consumer(this)
    new CodeSegment()
  }
}

object CodeSegment {

  // Default consumer which just prints all instructions
  implicit def outputConsumer(codeSegment: CodeSegment): Unit
  = codeSegment.instructions foreach(println(_))
}
