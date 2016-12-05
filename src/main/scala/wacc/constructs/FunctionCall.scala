package wacc.constructs

import wacc.arm._
import wacc.codegeneration._

case class FunctionCall(identifier: String, args: Seq[Expression], returnType: Type) extends AssignValue {
  override val vartype = returnType
  def getTypedName: String = Function.appendFunctionTypes(identifier, args map (_.vartype))

  override def transAssignRhs(registers: Seq[Register]): CodeSegment = {
    val argumentsSize = ImmOperand(args.map(_.vartype.size).sum)

    CodeSegment()
      .extend(args.reverse flatMap (e => {
        // Evaluate each argument and push them on stack in reverse order (first arg is closest to new frame)
        val argumentEvalInstructions = e.transAssignRhs(registers).instructions

        argumentEvalInstructions :+ (e.vartype match {
          case Character | Boolean => STRB(registers.head, RegisterAddress(SP, -e.vartype.size, writeback = true))
          case _                   => STR(registers.head, RegisterAddress(SP, -e.vartype.size, writeback = true))
        })
      }))
      .extend(BL(Label(getTypedName)))
      .extend(ADD(SP, SP, argumentsSize))
      .extend(MOV(registers.head, R0))
  }
}
