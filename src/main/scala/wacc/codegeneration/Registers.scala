package wacc.codegeneration

trait Address
case class IntAddress(address: Int) extends Address
case class RegisterAddress(register: Register, offset: Int) extends Address
case class Const(value: Int) extends Address
case class LabelAddress(label: String) extends Address

trait Operand
case class ImmOperand(value: Int) extends Operand
case class CharOperand(value: Char) extends Operand //discuss

class Register(name: String) extends Operand {
  override def toString: String = name
}

case class GPRegister(index: Int) extends Register("R" + index.toString)

object R0 extends GPRegister(0)
object R1 extends GPRegister(1)
object R2 extends GPRegister(2)
object R3 extends GPRegister(3)
object R4 extends GPRegister(4)
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

package object Registers {
  val expressionRegs: Seq[Register] = Seq(R4, R5, R6, R7, R8, R9, R10, R11, R12)
}
