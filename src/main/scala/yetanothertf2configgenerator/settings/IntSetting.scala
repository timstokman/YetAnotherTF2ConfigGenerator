package yetanothertf2configgenerator.settings

import scala.swing._
import event._

case class IntSetting(val name : String, val labelText : String, val tf2Class : String, val weapon : Int, val defaultValue : Int, val settingType : Symbol, val minValue : Option[Int], val maxValue : Option[Int]) extends SimpleGridBaseSetting[Int, TextField] {
  def validate = {
    try {
      val storedValue = parseValue(GUIStorage)
      minValue.map(_ <= storedValue).getOrElse(true) && maxValue.map(_ >= storedValue).getOrElse(true)
    } catch {
      case _ : java.lang.NumberFormatException => false
    }
  }

  def errorMessage = "'" + GUIStorage.text + "' has to be a number. " + minValue.map("Minimum value: " + _.toString + " ").getOrElse("") + maxValue.map("Maximum value: " + _.toString + " ").getOrElse("")

  def createGuiStorage = new TextField()

  def parseValue(guiStorage : TextField) = guiStorage.text.toInt

  def putValue(guiStorage : TextField, value : Int) {
    guiStorage.text = value.toString
  }

  def getReaction(fire : () => Unit) = {
    {
      case ValueChanged(_) => fire()
    }
  }
}
