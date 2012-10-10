package yetanothertf2configgenerator.settings

import scala.swing._
import event._

object NetworkConfigSetting {
  //list of network configs
  val networkConfigs = List("none", "bad", "good", "lan")
}

case class NetworkConfigSetting(val name : String, val labelText : String) extends AbstractChoiceSetting(NetworkConfigSetting.networkConfigs) {
  val settingType = 'options
  val defaultValue = Some("none")
  val weapon = 0
  val tf2Class = "any"
}
