package yetanothertf2configgenerator.settings

import scala.swing._
import collection.immutable.ListMap

object ClassAsIntListSetting {
  //map from class to the disguise number
  val numberToDisguise = ListMap(
    1 -> "scout",
    3 -> "soldier",
    7 -> "pyro",
    4 -> "demoman",
    6 -> "heavyweapons",
    9 -> "engineer",
    2 -> "sniper",
    5 -> "medic",
    8 -> "spy"
  )
}

case class ClassAsIntListSetting(val name : String, val tf2Class : String, val weapon : Int, val settingType : Symbol, val labelText : String) extends ListSetting[Int](ClassAsIntListSetting.numberToDisguise) { }
