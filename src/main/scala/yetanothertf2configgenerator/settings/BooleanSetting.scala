package yetanothertf2configgenerator.settings

import scala.swing._
import event._

case class BooleanSetting(val name : String, val labelText : String, val tf2Class : String, val weapon : Int, val defaultValue : Boolean, val settingType : Symbol) extends SimpleGridBaseSetting[Boolean, CheckBox] {
  def validate = true

  def errorMessage = ""

  def createGuiStorage = new CheckBox(labelText)

  override def createLabel : Option[Label] = None

  def parseValue(guiStorage : CheckBox) = guiStorage.selected

  def putValue(guiStorage : CheckBox, value : Boolean) {
    guiStorage.selected = value
  }

  override def getReaction(fire : () => Unit) = {
    {
      case ButtonClicked(_) => fire()
    }
  }
}