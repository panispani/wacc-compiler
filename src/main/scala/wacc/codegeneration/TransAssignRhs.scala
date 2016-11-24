package wacc.codegeneration

import wacc.SymbolTable
import wacc.arm._
import wacc.constructs._

/**
  * Created by panayiotis on 16/11/16.
  */
object TransAssignRhs {

  def transAssignRhs(value: AssignValue, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    value match {
      case e: Expression    => TransExpressions.transExpression(e, symbolTable, registers)
      case fc: FunctionCall => transFunctionCall(fc, symbolTable, registers)
      case default  => println("not implemented"); Seq()
    }
  }

  def transFunctionCall(fc: FunctionCall, symbolTable: SymbolTable, registers: Seq[Register]): Seq[Instruction] = {
    val argumentsSize = ImmOperand(fc.args.map(_.vartype.size).sum)
    CodeSegment()
      .extend(fc.args.reverse flatMap (e => {
        // Evaluate each argument and push them on stack in reverse order (first arg is closest to new frame)
        val argumentEvalInstructions = TransExpressions.transExpression(e, symbolTable, registers)
        argumentEvalInstructions :+ STR(registers.head, RegisterAddress(SP, -e.vartype.size, writeback = true))
      }))
      .append(BL(Label(fc.identifier)))
      .append(ADD(SP, SP, argumentsSize))
      .append(MOV(registers.head, R0))
      .instructions
  }
}
