package wacc.constructs

import wacc.{VariableReference}

case class Struct(identifier: String, members: Seq[VariableReference])