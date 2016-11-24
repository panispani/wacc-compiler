package wacc.constructs

import wacc.arm._

/**
  * Created by panayiotis on 08/11/16.
  */
case class UnaryOperator(unaryOperator: String) {
  def translate(dest: Register): Instruction = new Instruction {}
}

case class UnaryOperatorExpr(unaryOperator: UnaryOperator, expression: Expression) extends Expression {
  override val vartype = unaryOperator match {
    case NotOp | MinusOp => expression.vartype
    case LenOp | OrdOp => Integer
    case ChrOp => Character
  }
}

object NotOp extends UnaryOperator("!") {
  override def translate(dest: Register): Instruction = EOR(dest, dest, ImmOperand(1))
}
object MinusOp extends UnaryOperator("-") {
  override def translate(dest: Register): Instruction = RSBS(dest, dest, ImmOperand(0))
}
object LenOp extends UnaryOperator("len") {
  override def translate(dest: Register): Instruction = LDR(dest, RegisterAddress(dest, 0))
}

// TODO: Implement translate
object OrdOp extends UnaryOperator("ord")
object ChrOp extends UnaryOperator("chr")