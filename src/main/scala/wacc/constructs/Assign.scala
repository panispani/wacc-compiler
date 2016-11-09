package wacc.constructs

trait Typed {
  val vartype: Type
}

trait AssignValue extends Typed
trait AssignTarget extends Typed

case class Declare(vartype: Type, identifier: String, value: AssignValue) extends Statement {

  // TODO: If these checks really are necessary in the construct and not performed in a visitor
  // Then make these methods return Option[SemanticError] and call the method from visitor

  def checkTypes(target: Typed, value: Typed) = {
    target.vartype == value.vartype
  }

  def checkTypes(target: Typed, value: PairConstructor) = {
    target.vartype match {
      case pt: PairType => pt.firstType == value.firstExp.vartype &&
                           pt.secondType == value.secondExp.vartype
      case default      => false
    }
  }

  def checkTypes(target: Typed, value: PairElement) = {
  }

}

