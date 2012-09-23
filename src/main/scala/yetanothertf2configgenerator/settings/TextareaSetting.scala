package yetanothertf2configgenerator.settings

import scala.swing._
import event._
import GridBagPanel._

case class TextareaSetting(val name : String, val labelText : String, val tf2Class : String, val weapon : Int, val defaultValue : String, val settingType : Symbol) extends SimpleGridBaseSetting[String, TextArea] {
  def validate = true

  def errorMessage = ""

  def createGuiStorage = new TextArea {
    rows = 4
  }

  override def createGui(guiStorage : TextArea) = {
    new GridBagPanel {
      val c = new Constraints
      c.weightx = 0.5
      c.insets = new Insets(2, 2, 2, 2)
      c.anchor = Anchor.LineStart
      c.gridwidth = 2
      layout(errorLabel.getOrElse(Swing.HGlue)) = c
      c.gridwidth = 1
      c.gridy = 1
      layout(label.getOrElse(Swing.HGlue)) = c
      c.gridx = 1
      c.anchor = Anchor.LineEnd
      guiStorage.preferredSize = new Dimension(math.max(BaseSetting.minSize, guiStorage.preferredSize.width), guiStorage.preferredSize.height)
      layout(new ScrollPane(guiStorage) { verticalScrollBarPolicy = ScrollPane.BarPolicy.Always }) = c
    }
  }

  def parseValue(guiStorage : TextArea) = guiStorage.text

  def putValue(guiStorage : TextArea, value : String) {
    guiStorage.text = value
  }

  def getReaction(fire : () => Unit) = {
    {
      case ValueChanged(_) => fire()
    }
  }
}
