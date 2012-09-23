package yetanothertf2configgenerator.settings

import scala.swing._
import event._

case class WeaponDependencySetting[ValueType, GUIType <: Component](val createSetting : Int => Setting[ValueType, GUIType], val showMain : Boolean)(implicit gatheredManifest : Manifest[List[ValueType]]) extends DependencySetting[ValueType, GUIType, List[ValueType], Int](createSetting, "weapon", 1 to 3, 0, s => s.weapon)(gatheredManifest) {
  def value = dependingSettings.sortBy(_.weapon).map(_.value).toList

  def value_=(newValue : List[ValueType]) {
    for(i <- 0 until newValue.size) {
      dependingSettings(i).value = newValue(i)
    }
    setMainToCommon
  }
}
