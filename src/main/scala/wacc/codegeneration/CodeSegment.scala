package wacc.codegeneration

import wacc.arm.Instruction

class CodeSegment private(initial: Seq[Instruction]) {
  val instructions: Vector[Instruction] = initial.toVector

  def extend(instruction: Instruction) = {
    new CodeSegment(instructions :+ instruction)
  }

  def extend(extension: Seq[Instruction]) = {
    new CodeSegment(instructions ++ extension)
  }

  def extend(extension: CodeSegment) = {
    new CodeSegment(instructions ++ extension.instructions)
  }

  /**
    * The consumer will be a function composed of several stages
    * consumer = optimisation1 andThen ... andThen optimisationN andThen print
    *
    * See the companion object below for the default implementation
    * */
  def release()(implicit consumer: (CodeSegment => Unit)) = {
    consumer(this)
    CodeSegment()
  }
}

object CodeSegment {

  def apply(): CodeSegment = new CodeSegment(Vector())
  def apply(instruction: Instruction): CodeSegment = new CodeSegment(Vector(instruction))
  def apply(instructions: Instruction*): CodeSegment = new CodeSegment(instructions.toVector)

  // Default consumer which just prints all instructions
  implicit def outputConsumer(codeSegment: CodeSegment): Unit
  = codeSegment.instructions foreach(println(_))
}
