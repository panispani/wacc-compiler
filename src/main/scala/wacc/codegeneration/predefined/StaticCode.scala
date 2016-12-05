package wacc.codegeneration.predefined

import wacc.arm._
import wacc.codegeneration.CodeSegment
import wacc.codegeneration.predefined.std.StandardLibrary

/**
  * Created by tt1215 on 28/11/16.
  */

// TODO: Use a trait for expressing library APIs and hold a collection here
object StaticCode {
  val lib = StandardLibrary

  def outputFunctions: CodeSegment = {
    var functions = CodeSegment()
    for ((l, f) <- lib.requestedPredefinedFunctions) {
      functions = functions.extend(DefineLabel(l), NEW_STACK_FRAME).extend(f).extend(RETURN)
    }
    functions
  }

  def getStaticData(data: AsciiData): Label = {
    lib.staticDataMap(data)
  }

  def getStaticFunction(function: CodeSegment): Label = {
    val label = lib.staticFunctionMap(function)
    lib.requestedPredefinedFunctions += (label -> function)
    label
  }

  def outputData: CodeSegment = {
    var data = CodeSegment()
    for ((d, l) <- lib.staticDataMap) {
      data = data.extend(DefineLabel(l)).extend(d)
    }
    data
  }

}
