package wacc.codegeneration

import wacc.constructs._

/**
  * Created by panayiotis on 15/11/16.
  */
trait Register
trait Operand
trait Instruction

case class Add(Rd: Register, Rn: Register, Op2: Operand) extends Instruction

class irCodegenerator {
  def codegen(a: Any): Seq[Instruction] = {
    a match {
      case Program(functions, instructions) => for (instr <- instructions) codegen(instr)
      case Function(ident, params, vartype, stmt) => ;
      case IntegerLiteral(value: Integer) => ;
      case DeclareStatement(vartype: Type, identifier: String, value: AssignValue) => println("declare");
      case default => println("nope")
    }

    Seq[Instruction]()
  }
}
