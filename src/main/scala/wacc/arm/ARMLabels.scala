package wacc.arm

import wacc.codegeneration.CodeSegment

import scala.collection.mutable

case class Label(name: String) {
  override def toString: String = name

}

package object LabelTable {
  val labels: mutable.Buffer[DefineStringLabel] = mutable.Buffer()

  def outputLabels: CodeSegment = {
    new CodeSegment()
      .extend(labels)
  }
}

object Label {
  private var currentLabelNumber: Int = -1

  def apply(): Label = {
    currentLabelNumber += 1
    Label("L" + currentLabelNumber)
  }

  def clear(): Unit = { currentLabelNumber = -1 }
}

case class DefineLabel(label: Label) extends Instruction {
  override val name: String = s"$label:"

}

case class DefineStringLabel(string: String) extends Instruction {
  val label: Label = Label()
  override val name: String = s"$label: " + AsciiData(string)

  LabelTable.labels.append(this)
}
