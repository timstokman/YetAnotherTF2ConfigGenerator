package yetanothertf2configgenerator.settings

import scala.swing._
import event._

case class ClassDependencySetting[ValueType, GUIType <: Component](createSetting : String => Setting[ValueType, GUIType], val showMain : Boolean)(implicit gatheredManifest : Manifest[Map[String, ValueType]]) extends DependencySetting[ValueType, GUIType, Map[String, ValueType], String](createSetting, "class", Setting.classes, "any", s => s.tf2Class)(gatheredManifest) {
  def value = dependingSettings.map(s => (s.tf2Class, s.value)).toMap

  def value_=(newValue : Map[String, ValueType]) {
    newValue.foreach(pair => {
      dependingSettings.filter(_.tf2Class == pair._1).foreach(setting => {
	setting.value = pair._2
      })
    })
    setMainToCommon
  }
}
