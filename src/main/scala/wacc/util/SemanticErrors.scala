package wacc.util

import wacc.constructs.{SemanticError, Type}

object SemanticErrors {

  def typeError(what: String, got: Type, expected: Type*): String = {
    "expected " + what + " of type " + (expected mkString " or ") + ", got " + got
  }
}
