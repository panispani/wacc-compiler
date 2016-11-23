package wacc.arm

import wacc.codegeneration.CodeSegment

object Macros {

  /**
    * Returns the code for opening and closing a scope
    * If the scope is a function call the isBranch flag must be set to true
    * so that the LR and PC are handled appropriately
    * */
  //    * TODO: make functionally
def frame(size: Int, isBranch: Boolean = false): (CodeSegment, CodeSegment) = {
    val MAX_SIZE = 1024

    var start = new CodeSegment()

    if (isBranch) start = start.append(PUSH(Seq(LR)))
    start = start.extend(Seq(PUSH(Seq(FP)), MOV(FP, SP)))

    var end = new CodeSegment()

    // Handle large scopes by adding / subtracting several times
    val blocks = size / MAX_SIZE
    val remainder = size % MAX_SIZE

    for (i <- 1 to blocks) {
      start = start.append(SUB(SP, SP, ImmOperand(MAX_SIZE)))
      end = end.append(ADD(SP, SP, ImmOperand(MAX_SIZE)))
    }

    start = start.append(SUB(SP, SP, ImmOperand(remainder)))
    end = end.append(ADD(SP, SP, ImmOperand(remainder)))
             .append(POP(Seq(FP)))

    if (isBranch) end = end.append(POP(Seq(PC)))
    (start, end)
  }
}