package wacc.codegeneration

import wacc.constructs._

/**
  * Created by panayiotis on 16/11/16.
  */
package object Weight {
  def weight(e: Expression): Integer = {
    e match {
      case BinaryOperatorExpr(e1, binOp, e2) => {
        val e1Weight = weight(e1)
        val e2Weight = weight(e2)
        val cost1 = math.max(e1Weight, e2Weight + 1)
        val cost2 = math.max(e1Weight + 1, e2Weight)
        math.min(cost1, cost2)
      }
      case default => 1
    }
    0
  }
}
