package yetanothertf2configgenerator.settings

import scala.swing._
import event._

abstract class SettingEvent[ValueType, GUIType <: Component]

case class SettingChanged[ValueType, GUIType <: Component](oldValue : ValueType, newValue : ValueType) extends SettingEvent[ValueType, GUIType]
