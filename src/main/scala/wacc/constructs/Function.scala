package wacc.constructs

import wacc.Symbol

case class Function(identifier: Identifier,
                    params: Seq[Param],
                    returns: Type,
                    stmt: Statement) extends SemanticallyCheckable with Symbol {
//not base type

}
