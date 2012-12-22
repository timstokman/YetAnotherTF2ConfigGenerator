package yetanothertf2configgenerator.settings

import scala.swing._
import event._
import Reactions.Reaction

object BaseSetting {
  val minSize = 120
}

abstract class BaseSetting[ValueType, GUIType <: Component, GUIStorage]()(implicit valueManifest : Manifest[ValueType]) extends Setting[ValueType, GUIType] {
  protected var currentValue : ValueType = null.asInstanceOf[ValueType]

  val canSubscribe = false
  def defaultValue : Option[ValueType]
  def innerName = name
  def canSubscribeTo(setting : Setting[_, _ <: Component]) = false
  def errorMessage : String
  def parseValue(guiStorage : GUIStorage) : ValueType
  def putValue(guiStorage : GUIStorage, value : ValueType)
  def createGuiStorage : GUIStorage
  def createErrorLabel : Option[Label] = Some(new Label(errorMessage) { visible = false; background = java.awt.Color.RED; opaque = true })
  def createLabel : Option[Label] = Some(new Label(labelText))
  def getReaction(fire : () => Unit) : Reaction
  def putReaction(reaction : Reaction, guiStorage : GUIStorage)
  def createGui(guiStorage : GUIStorage) : GUIType
  
  def validateAndError = {
    if(validate) {
      errorLabel.foreach(_.visible = false)
      true
    } else {
      errorLabel.foreach(label => {
        label.text = errorMessage
        label.visible = true
      })
      false
    }
  }

  def onChange() {
    if(validateAndError) {
      val oldValue = currentValue
      currentValue = parseValue(GUIStorage)
      publish(SettingChanged(oldValue, currentValue))
      errorLabel.foreach(_.visible = false)
    }
  }

  def valueClass = valueManifest.getClass

  def notify(publisher : Setting[_, _ <: Component], event : SettingEvent[_, _ <: Component]) { }

  val labelText : String

  lazy val label = createLabel
  lazy val errorLabel = createErrorLabel
  val templateVariableDeclaration = if(necessary) "<%@ val " + name + ": " + valueManifest.toString + " %>" else ""

  lazy val GUIStorage = {
    val el = createGuiStorage    
    defaultValue match {
      case Some(value) => {
        currentValue = value
        putValue(el, currentValue) 
      }
      case None => {
        currentValue = parseValue(el)
      }
    }
    
    putReaction(getReaction(onChange), el)
    el
  }

  lazy val GUI = createGui(GUIStorage)

  def value_=(newValue : ValueType) {
    putValue(GUIStorage, newValue)
    onChange
  }

  def value = currentValue
  
  def necessary = true
}
