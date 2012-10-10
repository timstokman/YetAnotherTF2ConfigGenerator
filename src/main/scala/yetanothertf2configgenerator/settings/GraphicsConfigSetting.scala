package yetanothertf2configgenerator.settings

import scala.swing._
import event._

object GraphicsConfigSetting {
  //list of graphics configs
  val graphicsConfigs = List("none", "highquality", "maxquality", "highframes", "maxframes", "dx9frames")
  
  //messages for each graphics config
  val graphicsConfigsInfo = Map("highframes" -> "Chris' highframes config, designed to get you a large performance boost\nLaunch options: \nIMPORTANT: Remove -dxlevel 81 from the launch options after the first launch!\nFullscreen: -dxlevel 81 -full -w WIDTH -h HEIGHT -console -novid -useforcedmparms -noforcemaccel -noforcemspd\nWindowed:   -dxlevel 81 -sw -w WIDTH -h HEIGHT -console -noborder -novid -useforcedmparms -noforcemaccel -noforcemspd",
                                "maxframes" ->  "Chris' maxframes config, designed to get you a large performance boost\nLaunch options: \nIMPORTANT: Remove -dxlevel 81 from the launch options after the first launch!\nFullscreen: -dxlevel 81 -full -w WIDTH -h HEIGHT -console -novid -useforcedmparms -noforcemaccel -noforcemspd\nWindowed:   -dxlevel 81 -sw -w WIDTH -h HEIGHT -console -noborder -novid -useforcedmparms -noforcemaccel -noforcemspd",
                                "highquality" -> "Chris' highquality config, designed to get you excellent quality\nLaunch options: \nIMPORTANT: Remove -dxlevel 98 from the launch options after the first launch!\nFullscreen: -dxlevel 98 -full -w WIDTH -h HEIGHT -console -novid -useforcedmparms -noforcemaccel -noforcemspd\nWindowed:   -dxlevel 98 -sw -w WIDTH -h HEIGHT -console -noborder -novid -useforcedmparms -noforcemaccel -noforcemspd",
                                "maxquality" -> "Chris' maxquality config, designed to get you excellent quality\nLaunch options: \nIMPORTANT: Remove -dxlevel 98 from the launch options after the first launch!\nFullscreen: -dxlevel 98 -full -w WIDTH -h HEIGHT -console -novid -useforcedmparms -noforcemaccel -noforcemspd\nWindowed:   -dxlevel 98 -sw -w WIDTH -h HEIGHT -console -noborder -novid -useforcedmparms -noforcemaccel -noforcemspd",
                                "dx9frames" -> "Chris' dx9frames config, designed to get you a large performance boost\nLaunch options: \nIMPORTANT: Remove -dxlevel 90 from the launch options after the first launch!\nFullscreen: -dxlevel 90 -full -w WIDTH -h HEIGHT -console -novid -useforcedmparms -noforcemaccel -noforcemspd\nWindowed:   -dxlevel 90 -sw -w WIDTH -h HEIGHT -console -noborder -novid -useforcedmparms -noforcemaccel -noforcemspd")
}

case class GraphicsConfigSetting(val name : String, val labelText : String) extends AbstractChoiceSetting(GraphicsConfigSetting.graphicsConfigs) {
  val settingType = 'options
  val weapon = 0
  val tf2Class = "any"
  val defaultValue = Some("none")

  override def extraMessage = GraphicsConfigSetting.graphicsConfigsInfo.get(value)
}
