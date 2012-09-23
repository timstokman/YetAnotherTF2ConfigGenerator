package yetanothertf2configgenerator.settings

import scala.swing._
import event._

case class IntAsBooleanSetting(val name : String, val labelText : String, val tf2Class : String, val weapon : Int, val defaultValue : Int, val settingType : Symbol) extends SimpleGridBaseSetting[Int, CheckBox] {
  def validate = true

  def errorMessage = ""

  def createGuiStorage = new CheckBox(labelText)

  override def createLabel : Option[Label] = None

  def parseValue(guiStorage : CheckBox) = if(guiStorage.selected) 1 else 0

  def putValue(guiStorage : CheckBox, value : Int) {
    guiStorage.selected = value == 1
  }

  override def getReaction(fire : () => Unit) = {
    {
      case ButtonClicked(_) => fire()
    }
  }
}
