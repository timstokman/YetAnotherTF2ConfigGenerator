package yetanothertf2configgenerator.settings

import scala.swing._
import event._
import Reactions.Reaction

abstract class AbstractChoiceSetting(var choices : Seq[String]) extends SimpleGridBaseSetting[String, ComboBox[String]] {
  def validate = choices.exists(_ == GUIStorage.selection.item)

  override def errorMessage = value + " has to be one of " + choices.mkString(", ")

  def refreshChoices(storage: ComboBox[String]) {
    storage.peer.removeAllItems
    choices.foreach(choice => storage.peer.addItem(choice))
    putValue(storage, choices.head)
    currentValue = choices.head
  }
  
  def createGuiStorage = {
    val storage = new ComboBox[String](List()) {
      peer.setModel(new javax.swing.DefaultComboBoxModel)
    }
    refreshChoices(storage)
    storage
  }

  def parseValue(guiStorage : ComboBox[String]) = guiStorage.selection.item

  def putValue(guiStorage : ComboBox[String], value : String) {
    guiStorage.selection.item = value
  }

  def getReaction(fire : () => Unit) = {
    {
      case SelectionChanged(_) => fire()
    }
  }

  override def putReaction(reaction : Reaction, guiStorage : ComboBox[String]) {
    guiStorage.selection.reactions += reaction
  }
}
