package wacc.util

import wacc.constructs.Type

object SemanticErrors {

  def typeError(what: String, got: String, expected: Seq[String]): String =
    "expected " + what + " of type " + (expected mkString " or ") + ", got " + got

  def typeError(what: String, got: String, expected: String): String =
    typeError(what, got, Seq(expected))

  def typeError(what: String, got: Type, expected: Seq[Type]): String =
    typeError(what, got.toString, expected map (_.toString))

  def typeError(what: String, got: Type, expected: Type): String =
    typeError(what, got, Seq(expected))

  def functionSignatureToString(name: String, parameterTypes: Seq[Type]): String = {
    s"$name(${parameterTypes.mkString(", ")})"
  }
}
