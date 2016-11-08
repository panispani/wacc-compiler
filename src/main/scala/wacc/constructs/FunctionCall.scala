package wacc.constructs

/**
  * Created by ema on 08/11/2016.
  */
case class FunctionCall(identifier: Identifier, args: Seq[Expression]) extends SemanticallyCheckable {

}
