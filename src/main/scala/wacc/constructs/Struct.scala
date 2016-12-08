package wacc.constructs

import wacc.VariableReference

/**
  * The members are variable references and their offset is respective to the start of the struct
  */
case class Struct(identifier: String, parentName: Option[String], members: Seq[VariableReference])