package wacc

import wacc.codegeneration._
import wacc.constructs._

/**
  * Created by panayiotis on 16/11/16.
  */
package object TransFunctions {
  def transFunction(ident: String, params: Seq[Param], vartype: Type, stmt: Seq[Statement], registers: Seq[Register]): Seq[Instruction] = {
    val instruction = Seq[Instruction]()
    instruction
  }
}
