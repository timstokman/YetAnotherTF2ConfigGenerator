package yetanothertf2configgenerator.settings

import scala.swing._
import event._

case class StringSetting(val name : String, val labelText : String, val tf2Class : String, val weapon : Int, val defaultValue : String, val settingType : Symbol) extends SimpleGridBaseSetting[String, TextField] {
  def validate = true

  def errorMessage = ""

  def createGuiStorage = new TextField()

  def parseValue(guiStorage : TextField) = guiStorage.text

  def putValue(guiStorage : TextField, value : String) {
    guiStorage.text = value
  }

  def getReaction(fire : () => Unit) = {
    {
      case ValueChanged(_) => fire()
    }
  }
}
