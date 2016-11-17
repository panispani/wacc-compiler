package wacc

import wacc.constructs._
import wacc.codegeneration._

import TransStatements._
import TransFunctions._
import TransPrograms._
import TransAssigns._
import TransExpressions._



/**
  * Created by panayiotis on 16/11/16.
  */
package object TransFunctions {
  def transFunction(ident: String, params: Seq[Param], vartype: Type, stmt: Seq[Statement], registers: Seq[Register]): Seq[Instruction] = {
    val instruction = Seq[Instruction]()
    instruction
  }
}
