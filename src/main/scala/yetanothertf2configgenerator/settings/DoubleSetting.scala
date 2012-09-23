package yetanothertf2configgenerator.settings

import scala.swing._
import event._

case class DoubleSetting(val name : String, val labelText : String, val tf2Class : String, val weapon : Int, val defaultValue : Double, val settingType : Symbol, val minValue : Option[Double], val maxValue : Option[Double]) extends SimpleGridBaseSetting[Double, TextField] {
  def validate = {
    try {
      val storedValue = parseValue(GUIStorage)
      minValue.map(_ <= storedValue).getOrElse(true) && maxValue.map(_ >= storedValue).getOrElse(true)
    } catch {
      case _ : java.lang.NumberFormatException => false
    }
  }

  def errorMessage = "'" + GUIStorage.text + "' has to be a number with a . as fraction separator. " + minValue.map("Minimum value: " + _.toString + " ").getOrElse("") + maxValue.map("Maximum value: " + _.toString + " ").getOrElse("")

  def createGuiStorage = new TextField()

  def parseValue(guiStorage : TextField) = guiStorage.text.toDouble

  def putValue(guiStorage : TextField, value : Double) {
    guiStorage.text = value.toString
  }

  def getReaction(fire : () => Unit) = {
    {
      case ValueChanged(_) => fire()
    }
  }
}
