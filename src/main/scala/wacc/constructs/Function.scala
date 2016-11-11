package wacc.constructs

case class Function(identifier: String,
                    params: Seq[Param],
                    vartype: Type,
                    stmt: Seq[Statement])