package wacc.codegeneration

import wacc.constructs.{ArrayType, PairType, PrimitiveType, Type}

/**
  * Created by panayiotis on 16/11/16.
  */
object VarLog {
  private var bytes: Integer = 0
  private var vars: Integer = 0
//  def add(name: String, vartype: Type): Unit = {
//    val varsize = vartype match {
//      case Integer => 4
//      case Boolean => 1
//      case Character => 1
//      case String => 4 //keep on heap, this is a pointer
//      case ArrayType(elemtype: Type) => 4 //keep on heap
//      case PairType(ftype, sType) => 4 //keep on heap
//    }
//    vars = vars + 1
//    bytes = bytes + varsize
//  }
  def varCount(): Integer = {
    vars
  }
  def byteCount(): Integer = {
    bytes
  }
}
