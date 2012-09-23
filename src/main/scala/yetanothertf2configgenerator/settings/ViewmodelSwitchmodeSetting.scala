package yetanothertf2configgenerator.settings

import scala.swing._
import event._

object ViewmodelSwitchmodeSetting {
  val switchModes = List("immediately", "after-shooting", "after-strafing", "both")
}

case class ViewmodelSwitchmodeSetting(val name : String, val labelText : String, val tf2Class : String, val weapon : Int) extends AbstractChoiceSetting(ViewmodelSwitchmodeSetting.switchModes) {
  val settingType = 'options
  val defaultValue = "both"
}
