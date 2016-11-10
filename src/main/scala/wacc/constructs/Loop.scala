package wacc.constructs

case class Loop(condition: Expression, statements: Seq[Statement]) extends Statement {
}
