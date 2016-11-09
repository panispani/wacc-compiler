package wacc.constructs

import wacc.Symbol

case class Function(identifier: String,
                    params: Seq[Param],
                    vartype: Type,
                    stmt: Seq[Statement]) extends SemanticallyCheckable with Symbol {

}
