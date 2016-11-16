package wacc

import wacc.constructs._
import wacc.codegeneration._
import transStatements._
import transFunctions._
import transPrograms._
import transAssigns._
import transExpressions._


/**
  * Created by panayiotis on 16/11/16.
  */
package object transFunctions {
  def transFunction(ident: String, params: Seq[Param], vartype: Type, stmt: Seq[Statement], registers: Seq[Register]): Seq[Instruction] = {
    val instruction = Seq[Instruction]()
    instruction
  }
}
