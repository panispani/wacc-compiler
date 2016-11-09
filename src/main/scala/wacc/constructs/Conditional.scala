package wacc.constructs

case class Conditional(expression: Expression,
                       trueStatements: Seq[Statement],
                       falseStatements: Seq[Statement]) {

}
