package wacc.codegeneration

import wacc.constructs._

/**
  * Created by panayiotis on 15/11/16.
  */
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
