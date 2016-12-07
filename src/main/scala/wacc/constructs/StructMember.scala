package wacc.constructs

import wacc.{SymbolTable, VariableReference}

case class StructMember(vr: VariableReference, membersName: Seq[String]) extends Expression {

  override val varType: Type = {
    var currentStruct = vr
    for (memberName <- membersName) {
      currentStruct.varType match {
        case StructType(structId, _) => {
          currentStruct = SymbolTable.structsTable(structId).members.find(vr => vr.name == memberName).get
        }
      }
    }

    currentStruct.varType
  }

  val membersOffset: Seq[Int] = {
    var currentVr = vr
    var result: Seq[Int] = Seq()

    for (memberName <- membersName) {
      currentVr.varType match {
        case StructType(structId, _) => {
             val s = SymbolTable.structsTable(structId)
              val memberOffset = s.members
                .takeWhile(m => m.name != memberName)
                .map(m => m.varType.size)
                .sum
              result = result :+ memberOffset

          currentVr = SymbolTable.structsTable(structId).members.find(vr => vr.name == memberName).get
        }
      }
    }

    result
  }
}
