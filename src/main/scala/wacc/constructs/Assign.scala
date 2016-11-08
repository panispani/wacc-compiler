package wacc.constructs

case class Typed(vartype: Type)

trait AssignValue extends Typed
trait AssignTarget extends Typed

case class Assign(target: AssignTarget, value: AssignValue) extends SemanticallyCheckable {

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


  override def semanticCheck(): Unit = {
    checkTypes(target, value)
  }
}

