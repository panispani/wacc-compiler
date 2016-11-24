package wacc.constructs

import wacc.arm._
import wacc.codegeneration.CodeSegment

/**
  * Created by panayiotis on 08/11/16.
  */
abstract class UnaryOperator(unaryOperator: String) {
  def translate(dest: Register): CodeSegment
}

case class UnaryOperatorExpr(unaryOperator: UnaryOperator, expression: Expression) extends Expression {
  override val vartype = unaryOperator match {
    case NotOp | MinusOp => expression.vartype
    case LenOp | OrdOp => Integer
    case ChrOp => Character
  }
}

object UnaryOperator {
  def fromOperatorString(operator: String): UnaryOperator = operator match {
    case "!" => NotOp
    case "-" => MinusOp
    case "len" => LenOp
    case "ord" => OrdOp
    case "chr" => ChrOp
  }
}

object NotOp extends UnaryOperator("!") {
  override def translate(dest: Register): CodeSegment = CodeSegment(EOR(dest, dest, ImmOperand(1)))
}
object MinusOp extends UnaryOperator("-") {
  override def translate(dest: Register): CodeSegment = CodeSegment(RSBS(dest, dest, ImmOperand(0)))
}
object LenOp extends UnaryOperator("len") {
  override def translate(dest: Register): CodeSegment = CodeSegment(LDR(dest, RegisterAddress(dest, 0)))
}

// TODO: Implement translate
object OrdOp extends UnaryOperator("ord") {
  override def translate(dest: Register): CodeSegment = CodeSegment()
}
object ChrOp extends UnaryOperator("chr") {
  override def translate(dest: Register): CodeSegment = CodeSegment()
}