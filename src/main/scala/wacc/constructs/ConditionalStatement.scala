package wacc.constructs

case class ConditionalStatement(expression: Expression,
                                trueStatements: Seq[Statement],
                                falseStatements: Seq[Statement]) extends Statement {

}
