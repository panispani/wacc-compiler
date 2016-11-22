package wacc.arm

trait Operand
case class ImmOperand(value: Int) extends Operand {
  override def toString: String = s"#$value"
}
case class CharOperand(value: Char) extends Operand {
  override def toString: String = s"'$value'"
}

trait Address extends Operand
case class ImmAddress(address: Int) extends Address {
  override def toString: String = s"[${ImmOperand(address)}]"
}

case class RegisterAddress(register: Register, offset: Int) extends Address {
  override def toString: String = s"[$register, ${ImmOperand(offset)}]"
}

case class LabelAddress(label: Label) extends Address {
  override def toString: String = s"=$label"
}

case class Const(value: Int) extends Address {
  override def toString: String = s"=$value"
}

