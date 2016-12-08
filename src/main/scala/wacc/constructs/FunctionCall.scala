package wacc.constructs

import wacc.arm._
import wacc.codegeneration._

case class FunctionCall(name: String, args: Seq[Expression], returnType: Type) extends AssignValue {

  override val varType: Type = returnType
  def identifier: String = Function.fullName(name, args map (_.varType))

  override def transAssignRhs(registers: Seq[Register]): CodeSegment = {
    val argumentsSize = ImmOperand(args.map(_.varType.size).sum)

    CodeSegment()
      .extend(args.reverse flatMap (e => {
        // Evaluate each argument and push them on stack in reverse order (first arg is closest to new frame)
        val argumentEvalInstructions = e.transAssignRhs(registers).instructions

        argumentEvalInstructions :+ (e.varType match {
          case Character | Boolean => STRB(registers.head, RegisterAddress(SP, -e.varType.size, writeback = true))
          case _                   => STR(registers.head, RegisterAddress(SP, -e.varType.size, writeback = true))
        })
      }))
      .extend(BL(Label(identifier)))
      .extend(ADD(SP, SP, argumentsSize))
      .extend(MOV(registers.head, R0))
  }
}
