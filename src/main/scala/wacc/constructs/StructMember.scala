package wacc.constructs

import wacc.{SymbolTable, VariableReference}

case class StructMember(struct: VariableReference, membersName: Seq[String]) extends Expression {

  override val varType: Type = {
    var currentStruct = struct
    for (memberName <- membersName) {
      currentStruct.varType match {
        case StructType(structId, _) => {
          currentStruct = SymbolTable.structsTable(structId).members.filter(vr => vr.name == memberName).head
        }
      }
    }

    currentStruct.varType
  }
}
