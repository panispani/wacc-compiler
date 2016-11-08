package wacc.constructs

import wacc.Symbol

case class Variable(identifier: Identifier, vartype: Type) extends AssignTarget with Symbol
