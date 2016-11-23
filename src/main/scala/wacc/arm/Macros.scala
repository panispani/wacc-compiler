package wacc.arm

import wacc.codegeneration.CodeSegment

object Macros {

  /**
    * Returns the code for opening and closing a scope
    * If the scope is a function call the isBranch flag must be set to true
    * so that the LR and PC are handled appropriately
    * */
  def frame(size: Int, isBranch: Boolean = false): (CodeSegment, CodeSegment) = {
    val MAX_SIZE = 1024

    val start = new CodeSegment()

    if (isBranch) start.append(PUSH(Seq(LR)))
    start.extend(Seq(PUSH(Seq(FP)), MOV(FP, SP)))

    val end = new CodeSegment()

    // Handle large scopes by adding / subtracting several times
    val blocks = size / MAX_SIZE
    val remainder = size % MAX_SIZE

    for (i <- 0 to blocks) {
      start.append(SUB(SP, SP, ImmOperand(MAX_SIZE)))
      end.append(ADD(SP, SP, ImmOperand(MAX_SIZE)))
    }

    start.append(SUB(SP, SP, ImmOperand(remainder)))
    end.append(ADD(SP, SP, ImmOperand(remainder)))
      .append(POP(Seq(FP)))

    if (isBranch) end.append(POP(Seq(PC)))

    (start, end)
  }
}