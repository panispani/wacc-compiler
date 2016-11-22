package wacc.arm

trait Operand
case class ImmOperand(value: Int) extends Operand {
  override def toString: String = s"#$value"
}
case class CharOperand(value: Char) extends Operand {
  override def toString: String = s"'$value'"
}

trait Address extends Operand
case class IntAddress(address: Int) extends Address
case class RegisterAddress(register: Register, offset: Int) extends Address
case class Const(value: Int) extends Address
case class LabelAddress(label: String) extends Address

