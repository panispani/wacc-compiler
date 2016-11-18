package wacc.codegeneration

trait Register

trait Address
case class IntAddress(address: Int) extends Address
case class RegisterAddress(register: Register) extends Address

trait Operand
case class ImmOperand(value: Int) extends Operand
case class RegisterOperand(register: Register) extends Operand
case class CharOperand(value: Char) extends Operand //discuss

object R0 extends Register
object R1 extends Register
object R2 extends Register
object R3 extends Register
object R4 extends Register
object R5 extends Register
object R6 extends Register
object R7 extends Register
object R8 extends Register
object R9 extends Register
object R10 extends Register
object R11 extends Register
object R12 extends Register
object SP extends Register
object LR extends Register
object PC extends Register
