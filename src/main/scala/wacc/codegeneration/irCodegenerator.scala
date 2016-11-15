package wacc.codegeneration

import wacc.constructs._

/**
  * Created by panayiotis on 15/11/16.
  */
trait Register
object R1 extends Register
object R2 extends Register
object R3 extends Register
object R4 extends Register
object R5 extends Register
object R6 extends Register
object R7 extends Register
object R8 extends Register
object R9 extends Register
object R10 extends Register
object R11 extends Register
object R12 extends Register
object R13 extends Register
object R14 extends Register
//r1 to r14

trait Operand

trait Instruction
case class Add(Rd: Register, Rn: Register, Op2: Operand) extends Instruction


//accumulator register approach
class irCodegenerator {
  val allRegisters = Seq(R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14)

  def codegen(program: Program): Seq[Instruction] = {
    transNext(program, allRegisters)
  }

  def transNext(a: Any, registers: Seq[Register]): Seq[Instruction] = {
    a match {
      case Program(functions, stmt)
        => transProgram(functions, stmt, registers)
      case default
        => println("throw an error")
    }

    Seq[Instruction]()
  }

  def transProgram(functions: Seq[Function], stmts: Seq[Statement], registers: Seq[Register]): Seq[Instruction] = {
    val instruction = Seq[Instruction]()
    for (f <- functions) instruction ++ transNext(f, registers)
    for (stmt <- stmts) instruction ++ transNext(stmt, registers)
    instruction
  }



}
