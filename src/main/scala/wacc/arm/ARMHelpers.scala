package wacc.arm

object RETURN extends MOV(PC, LR)
object NEW_STACK_FRAME extends PUSH(Seq(LR))
