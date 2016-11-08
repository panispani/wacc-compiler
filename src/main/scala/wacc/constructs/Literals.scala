package wacc.constructs

import sun.reflect.generics.tree.BaseType

trait Literal extends AssignValue

case class ArrayLiteral(elemtype: BaseType, elements: List[BaseType]) extends Literal


