package wacc.constructs

import wacc.VariableReference

case class Function(identifier: String,
                    params: Seq[VariableReference],
                    vartype: Type,
                    stmt: Seq[Statement])