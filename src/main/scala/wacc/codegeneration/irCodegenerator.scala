package wacc.codegeneration

import wacc.constructs._
import wacc.codegeneration.Weight.weight

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
      case Function(ident, params, vartype, stmt)
        => transFunction(ident, params, vartype, stmt, registers)
      case DeclareStatement(vartype, identifier, value)
        => transDeclareStatement(vartype, identifier, value, registers)
      case default
        => println("can we even reach this point"); Seq[Instruction]()
    }
  }

  def transProgram(functions: Seq[Function], stmts: Seq[Statement], registers: Seq[Register]): Seq[Instruction] = {
    val instruction = Seq[Instruction]()
    for (f <- functions) instruction ++ transNext(f, registers)
    for (stmt <- stmts) instruction ++ transNext(stmt, registers)
    instruction
  }

  def transFunction(ident: String, params: Seq[Param], vartype: Type, stmt: Seq[Statement], registers: Seq[Register]): Seq[Instruction] = {
    val instruction = Seq[Instruction]()
    instruction
  }

  def transDeclareStatement(vartype: Type, identifier: String, value: AssignValue, registers: Seq[Register]): Seq[Instruction] = {
    val instruction = Seq[Instruction]()
    
    println("declare " + identifier + " to be " + value + "(" + vartype + ")" )
    instruction
  }

  //expressions
  def transExpression(expr: Expression): Seq[Instruction] = {
    expr match {
      case BinaryOperatorExpr(e1, bOp, e2) => {
        if (weight(e1) > weight(e2)) {
          // e1 first
        } else {
          // e2 first
        }
      }
    }
    Seq()
  }


}
