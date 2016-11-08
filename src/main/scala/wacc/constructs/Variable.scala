package wacc.constructs

import wacc.Symbol

case class Variable(identifier: String, vartype: Type) extends AssignTarget with Symbol
