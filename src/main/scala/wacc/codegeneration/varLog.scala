package wacc.codegeneration

import wacc.constructs.{ArrayType, PairType, PrimitiveType, Type}

/**
  * Created by panayiotis on 16/11/16.
  */
object varLog {
  private var bytes: Integer = 0
  private var vars: Integer = 0
  def add(name: String, location: Integer, vartype: Type): Unit = {
    val varsize = vartype match {
      case PrimitiveType("int") => 4
      case PrimitiveType("bool") => 1
      case PrimitiveType("char") => 1
      case PrimitiveType("string") => 4 //keep on heap, this is a pointer
      case ArrayType(elemtype: Type) => 4 //keep on heap
      case PairType(ftype, sType) => 4 //keep on heap
    }
    vars = vars + 1
    bytes = bytes + varsize
  }
  def varCount(): Integer = {
    vars
  }
  def byteCount(): Integer = {
    bytes
  }
}
