package wacc.codegeneration

trait Operand

case class ImmOperand(value: Int) extends Operand
case class RegisterOperand(register: Register) extends Operand

class Register(name: String) {
  override def toString: String = name
}

case class GPRegister(index: Int) extends Register("R" + index.toString)

object R0 extends GPRegister(0)
object R2 extends GPRegister(1)
object R3 extends GPRegister(2)
object R4 extends GPRegister(3)
object R1 extends GPRegister(4)
object R5 extends GPRegister(5)
object R6 extends GPRegister(6)
object R7 extends GPRegister(7)
object R8 extends GPRegister(8)
object R9 extends GPRegister(9)
object R10 extends GPRegister(10)
object R11 extends GPRegister(11)
object R12 extends GPRegister(12)
object SP extends Register("SP")
object LR extends Register("LR")
object PC extends Register("PC")