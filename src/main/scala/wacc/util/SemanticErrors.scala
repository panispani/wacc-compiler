package wacc.util

import wacc.constructs.{SemanticError, Type}

object SemanticErrors {

  def typeError(what: String, got: String, expected: String*): String =
    "expected " + what + " of type " + (expected mkString " or ") + ", got " + got

  def typeError(what: String, got: Type, expected: Type*): String =
    typeError(what, got.toString, expected.toString)
}
