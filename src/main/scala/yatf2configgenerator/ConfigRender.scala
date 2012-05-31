package yatf2configgenerator

import org.fusesource.scalate._
import scala.collection.immutable.ListMap
import java.io.{ FileOutputStream, OutputStreamWriter, File }

class ConfigRender {
  val engine = new TemplateEngine

  val templateDir = "templates"

  //list of templates
  val configNames = List(
    "autoexec",
    "reset",
    "scout",
    "engineer",
    "pyro",
    "soldier",
    "heavyweapons",
    "sniper",
    "spy",
    "medic",
    "demoman",
    "dx9frames",
    "highframes",
    "maxframes",
    "highquality",
    "maxquality",
    "net")

  //list of valid tf2 key codes
  val validKeyStrings = List(
    "nothing",
    "MWHEELUP",
    "MWHEELDOWN",
    "MOUSE1",
    "MOUSE2",
    "MOUSE3",
    "MOUSE4",
    "MOUSE5",
    "`", ".", ",",
    "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
    "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12",
    "SHIFT",
    "CTRL",
    "SPACE",
    "ALT",
    "RSHIFT",
    "RCTRL",
    "TAB",
    "BACKSPACE",
    "SEMICOLOR",
    "INS",
    "HOME",
    "SCROLLLOCK",
    "NUMLOCK",
    "END",
    "DEL",
    "PGUP",
    "PGDOWN",
    "UPARROW",
    "DOWNARROW",
    "LEFTARROW",
    "RIGHTARROW",
    "KP_INS",
    "KP_END",
    "KP_DOWNARROW",
    "KP_PGDN",
    "KP_LEFTARROW",
    "KP_5",
    "KP_RIGHTARROW",
    "KP_HOME",
    "KP_UPARROW",
    "KP_PGUP",
    "KP_ENTER",
    "PK_DEL",
    "KP_MINUS",
    "KP_PLUS",
    "KP_MULTIPLY",
    "KP_SLASH")

  //map from class to the disguise number
  val disguiseClassNumberMap = ListMap("scout" -> 1,
    "soldier" -> 3,
    "pyro" -> 7,
    "demoman" -> 4,
    "heavyweapons" -> 6,
    "engineer" -> 9,
    "sniper" -> 2,
    "medic" -> 5,
    "spy" -> 8)
    
  //reversed disguise map
  val disguiseNumberClassMap = ListMap() ++ disguiseClassNumberMap.map(_.swap)

  //list of graphics configs
  val graphicsConfigs = List("none", "highquality", "maxquality", "highframes", "maxframes", "dx9frames")
  
  val graphicsConfigsInfo = Map("highframes" -> "Chris' highframes config, designed to get you a large performance boost\nLaunch options: \nIMPORTANT: Remove -dxlevel 81 from the launch options after the first launch!\nFullscreen: -dxlevel 81 -full -w WIDTH -h HEIGHT -console -novid -useforcedmparms -noforcemaccel -noforcemspd\nWindowed:   -dxlevel 81 -sw -w WIDTH -h HEIGHT -console -noborder -novid -useforcedmparms -noforcemaccel -noforcemspd",
                                "maxframes" ->  "Chris' maxframes config, designed to get you a large performance boost\nLaunch options: \nIMPORTANT: Remove -dxlevel 81 from the launch options after the first launch!\nFullscreen: -dxlevel 81 -full -w WIDTH -h HEIGHT -console -novid -useforcedmparms -noforcemaccel -noforcemspd\nWindowed:   -dxlevel 81 -sw -w WIDTH -h HEIGHT -console -noborder -novid -useforcedmparms -noforcemaccel -noforcemspd",
                                "highquality" -> "Chris' highquality config, designed to get you excellent quality\nLaunch options: \nIMPORTANT: Remove -dxlevel 98 from the launch options after the first launch!\nFullscreen: -dxlevel 98 -full -w WIDTH -h HEIGHT -console -novid -useforcedmparms -noforcemaccel -noforcemspd\nWindowed:   -dxlevel 98 -sw -w WIDTH -h HEIGHT -console -noborder -novid -useforcedmparms -noforcemaccel -noforcemspd",
                                "maxquality" -> "Chris' maxquality config, designed to get you excellent quality\nLaunch options: \nIMPORTANT: Remove -dxlevel 98 from the launch options after the first launch!\nFullscreen: -dxlevel 98 -full -w WIDTH -h HEIGHT -console -novid -useforcedmparms -noforcemaccel -noforcemspd\nWindowed:   -dxlevel 98 -sw -w WIDTH -h HEIGHT -console -noborder -novid -useforcedmparms -noforcemaccel -noforcemspd",
                                "dx9frames" -> "Chris' dx9frames config, designed to get you a large performance boost\nLaunch options: \nIMPORTANT: Remove -dxlevel 90 from the launch options after the first launch!\nFullscreen: -dxlevel 90 -full -w WIDTH -h HEIGHT -console -novid -useforcedmparms -noforcemaccel -noforcemspd\nWindowed:   -dxlevel 90 -sw -w WIDTH -h HEIGHT -console -noborder -novid -useforcedmparms -noforcemaccel -noforcemspd")

  //list of network configs
  val networkConfigs = List("none", "bad", "good", "lan")

  //list of crosshair types
  val crosshairTypes = List("none", "default", "crosshair1", "crosshair2", "crosshair3", "crosshair4", "crosshair5", "crosshair6", "crosshair7", "crosshair8", "crosshair9", "crosshair10", "crosshair11")

  /*
   * Option metadata necessary for UI generation
   * in the tuple:
   * * UI section
   * * Type of option
   * * Label for the option
   */
  val optionMetadata = ListMap(
    "dingEnable" -> ('options, 'intAsBoolean, "Enable weapon dingelings"),
    "dingVolume" -> ('options, 'double, "Dingeling volume"),
    "dingPitchMax" -> ('options, 'int, "Dingeling max pitch"),
    "dingPitchMin" -> ('options, 'int, "Dingeling min pitch"),
    "graphicsConfig" -> ('options, 'graphics, "Graphics config"),
    "networkConfig" -> ('options, 'network, "Network config"),
    "fov" -> ('options, 'int, "Fov"),
    "viewmodelFov" -> ('options, 'int, "Viewmodel fov"),
    "sensitivity" -> ('options, 'double, "Sensitivity"),
    "autoreload" -> ('options, 'intAsBoolean, "Autoreload"),
    "fastswitch" -> ('options, 'intAsBoolean, "Fastswitch"),
    "damagenumbers" -> ('options, 'intAsBoolean, "Damagenumbers"),
    "hideTracers" -> ('options, 'boolean, "Hide tracers"),
    "enableCrouchJump" -> ('options, 'boolean, "Enable crouchjump"),
    "crouchJumpNotFor" -> ('options, 'classList, "Exclude classes from crouchjump"),
    "scoreboardNetgraph" -> ('options, 'int, "Netgraph type with scoreboard"),
    "autocallThreshold" -> ('options, 'int, "Autocall threshold"),
    "uberPopChat" -> ('options, 'boolean, "Communicate uber through chat"),
    "showHealBeam" -> ('options, 'boolean, "Show viewmodel heal beam"),
    "fullChargeBell" -> ('options, 'intAsBoolean, "Sound when sniper rifle is charged"),
    "zoomSensitivity" -> ('options, 'double, "Zoom sensitivity (sniper)"),
    "zoomFactor" -> ('options, 'double, "Zoom factor"),
    "noCrossHairZoom" -> ('options, 'intAsBoolean, "Disable crosshair on zoom"),
    "showFlames" -> ('options, 'boolean, "Show viewmodel flames"),
    "disguiseCycleClasses" -> ('options, 'classDisguiseList, "Disguise cyclelist enemy classes"),
    "disguiseFriendlyCycleClasses" -> ('options, 'classDisguiseList, "Disguise cyclelist friendly classes"), 
    "precEnabled" -> ('options, 'boolean, "I Use P-Rec"),
    "precLog" -> ('options, 'int, "P-Rec log"),
    "precScreens" -> ('options, 'intAsBoolean, "P-Rec takes screenshots"),
    "precDeleteUseless" -> ('options, 'intAsBoolean, "P-Rec deletes useless demos"),
    "precMinStreak" -> ('options, 'int, "P-Rec minimum kill streak"),
    "precKillDelay" -> ('options, 'int, "P-Rec kill delay"),
    "precDir" -> ('options, 'string, "P-Rec demo directory"),
    "precMode" -> ('options, 'int, "P-Rec Mode"),
    "weaponColors" -> ('weapons, 'colorList, "Weapon color"),
    "weaponCrosshairs" -> ('weapons, 'crosshairList, "Weapon crosshair"),
    "weaponScales" -> ('weapons, 'scaleList, "Weapon scale"),
    "weaponShow" -> ('weapons, 'showList, "Weapon visible"),
    "classSpecificEnabled" -> ('weapons, 'classEnabledList, "Enable class specific weapon config"),
    "classWeaponColors" -> ('weapons, 'classColorList, "Class specific weapon color"),
    "classWeaponCrosshairs" -> ('weapons, 'classCrosshairs, "Class specific weapon crosshair"),
    "classWeaponScales" -> ('weapons, 'classScaleList, "Class specific weapon scale"),
    "classWeaponShow" -> ('weapons, 'classShowList, "Class specific weapon show"),
    "classSensitivity" -> ('weapons, 'classSensitivity, "Class specific weapon sensitivity"),
    "classDingPitchMax" -> ('weapons, 'classDingPitch, "Class specific weapon ding max pitch"),
    "classDingPitchMin" -> ('weapons, 'classDingPitch, "Class specific weapon ding min pitch"),
    "classDingVolume" -> ('weapons, 'classDingVolume, "Class specific weapon ding volume"),
    "movementFwd" -> ('binds, 'key, "Move forward"),
    "movementLeft" -> ('binds, 'key, "Move left"),
    "movementBack" -> ('binds, 'key, "Move backward"),
    "movementRight" -> ('binds, 'key, "Move right"),
    "primAttack" -> ('binds, 'key, "Primary attack"),
    "secAttack" -> ('binds, 'key, "Secondary attack"),
    "primSecSwap" -> ('binds, 'key, "Swap primary and secondary weapon"),
    "meleeSwap" -> ('binds, 'key, "Swap current and melee weapon"),
    "directWeapon1" -> ('binds, 'key, "Weapon 1"),
    "directWeapon2" -> ('binds, 'key, "Weapon 2"),
    "directWeapon3" -> ('binds, 'key, "Weapon 3"),
    "directWeapon4" -> ('binds, 'key, "Weapon 4"),
    "voiceMenu1" -> ('binds, 'key, "Voice menu 1"),
    "voiceMenu2" -> ('binds, 'key, "Voice menu 2"),
    "voiceMenu3" -> ('binds, 'key, "Voice menu 3"),
    "actionSlot" -> ('binds, 'key, "Action slot"),
    "taunt" -> ('binds, 'key, "Taunt"),
    "dropItem" -> ('binds, 'key, "Drop Item"),
    "inspect" -> ('binds, 'key, "Inspect"),
    "say" -> ('binds, 'key, "Chat"),
    "teamSay" -> ('binds, 'key, "Team chat"),
    "console" -> ('binds, 'key, "Console"),
    "duck" -> ('binds, 'key, "Duck"),
    "reloadhud" -> ('binds, 'key, "Reload/Fix hud"),
    "suicide" -> ('binds, 'key, "Suicide"),
    "screenshot" -> ('binds, 'key, "Screenshot"),
    "saveReplay" -> ('binds, 'key, "Save replay"),
    "quickSwitch" -> ('binds, 'key, "Quick item switch"),
    "teamOnlyTalk" -> ('binds, 'key, "Team only voice"),
    "normalTalk" -> ('binds, 'key, "Voice"),
    "medic" -> ('binds, 'key, "Call medic"),
    "scoreboard" -> ('binds, 'key, "Scoreboard"),
    "backpack" -> ('binds, 'key, "Show backpack"),
    "switchClass" -> ('binds, 'key, "Switch class"),
    "switchTeam" -> ('binds, 'key, "Switch team"),
    "medicRadar" -> ('binds, 'key, "Medic radar"),
    "fakeUber" -> ('binds, 'key, "Fake uber"),
    "jump" -> ('binds, 'key, "Jump (possibly with crouch)"),
    "normalJump" -> ('binds, 'key, "Jump (always without crouch)"),
    "zoom" -> ('binds, 'key, "Zoom in"),
    "disguiseCycle" -> ('binds, 'key, "Cycle normal disguise"),
    "disguiseFriendlyCycle" -> ('binds, 'key, "Cycle friendly disguise"),
    "disguiseLast" -> ('binds, 'key, "Last disguise"),
    "disguiseScout" -> ('binds, 'key, "Disguise scout"),
    "disguiseSoldier" -> ('binds, 'key, "Disguise soldier"),
    "disguisePyro" -> ('binds, 'key, "Disguise pyro"),
    "disguiseDemo" -> ('binds, 'key, "Disguise demo"),
    "disguiseHeavy" -> ('binds, 'key, "Disguise heavy"),
    "disguiseEngineer" -> ('binds, 'key, "Disguise engineer"),
    "disguiseMedic" -> ('binds, 'key, "Disguise medic"),
    "disguiseSniper" -> ('binds, 'key, "Disguise sniper"),
    "disguiseSpy" -> ('binds, 'key, "Disguise spy"),
    "buildSentry" -> ('binds, 'key, "Build sentry"),
    "buildDispenser" -> ('binds, 'key, "Build dispenser"),
    "buildEntrance" -> ('binds, 'key, "Build entrance"),
    "buildExit" -> ('binds, 'key, "Build exit"),
    "precMark" -> ('binds, 'key, "P-Rec mark"))

  /*
   * Template options, including some defaults
   */
  var options = collection.mutable.Map(
    "dingEnable" -> 1,
    "dingVolume" -> 1.5,
    "dingPitchMax" -> 60,
    "dingPitchMin" -> 120,
    "graphicsConfig" -> "none",
    "networkConfig" -> "good",
    "fov" -> 90,
    "viewmodelFov" -> 80,
    "movementFwd" -> "w",
    "movementLeft" -> "a",
    "movementBack" -> "s",
    "movementRight" -> "d",
    "autoreload" -> 1,
    "fastswitch" -> 1,
    "damagenumbers" -> 1,
    "sensitivity" -> 1.0,
    "hideTracers" -> true,
    "weaponColors" -> List((0, 255, 0), (0, 255, 0), (0, 255, 0), (0, 255, 0)),
    "weaponCrosshairs" -> List("crosshair5", "crosshair4", "crosshair7", "crosshair7"),
    "weaponScales" -> List(10, 15, 14, 14),
    "weaponShow" -> List(false, false, true, true),
    "classWeaponColors" -> Map[String, List[(Int, Int, Int)]](),
    "classWeaponCrosshairs" -> Map[String, List[String]](),
    "classWeaponScales" -> Map[String, List[Int]](),
    "classWeaponShow" -> Map[String, List[Boolean]](),
    "classSpecificEnabled" -> Map("soldier" -> false, "scout" -> false, "pyro" -> false, "demoman" -> false, "engineer" -> false, "heavyweapons" -> false, "sniper" -> false, "spy" -> false, "medic" -> false),
    "classSensitivity" -> Map[String, List[Double]](),
    "classDingPitchMax" -> Map[String, List[Int]](),
    "classDingPitchMin" -> Map[String, List[Int]](),
    "classDingVolume" -> Map[String, List[Double]](),
    "primSecSwap" -> "q",
    "meleeSwap" -> "r",
    "voiceMenu1" -> "z",
    "voiceMenu2" -> "x",
    "voiceMenu3" -> "c",
    "actionSlot" -> "h",
    "taunt" -> "g",
    "dropItem" -> "CTRL",
    "inspect" -> "f",
    "say" -> "y",
    "teamSay" -> "u",
    "console" -> "`",
    "duck" -> "SHIFT",
    "reloadhud" -> "DEL",
    "suicide" -> "INS",
    "screenshot" -> "HOME",
    "saveReplay" -> "END",
    "quickSwitch" -> "t",
    "teamOnlyTalk" -> "MOUSE4",
    "zoom" -> "MOUSE3",
    "zoomFactor" -> 2.5,
    "normalTalk" -> "v",
    "medic" -> "e",
    "scoreboard" -> "TAB",
    "directWeapon1" -> "1",
    "directWeapon2" -> "2",
    "directWeapon3" -> "3",
    "directWeapon4" -> "4",
    "scoreboardNetgraph" -> 4,
    "primAttack" -> "MOUSE1",
    "secAttack" -> "MOUSE2",
    "backpack" -> "m",
    "switchClass" -> ",",
    "switchTeam" -> ".",
    "enableCrouchJump" -> true,
    "crouchJumpNotFor" -> List("soldier", "demoman"),
    "medicRadar" -> "b",
    "autocallThreshold" -> 75,
    "fakeUber" -> "n",
    "uberPopChat" -> true,
    "fullChargeBell" -> 1,
    "zoomSensitivity" -> 1.0,
    "noCrossHairZoom" -> 0,
    "jump" -> "SPACE",
    "normalJump" -> "ALT",
    "showHealBeam" -> true,
    "showFlames" -> false,
    "disguiseCycle" -> "b",
    "disguiseCycleClasses" -> List(2, 7, 8, 9),
    "disguiseFriendlyCycle" -> "n",
    "disguiseFriendlyCycleClasses" -> List(5, 2, 9, 4, 7),
    "disguiseLast" -> "j",
    "disguiseScout" -> "F1",
    "disguiseSoldier" -> "F2",
    "disguisePyro" -> "F3",
    "disguiseDemo" -> "F4",
    "disguiseHeavy" -> "F5",
    "disguiseEngineer" -> "F6",
    "disguiseMedic" -> "F7",
    "disguiseSniper" -> "F8",
    "disguiseSpy" -> "F9",
    "buildSentry" -> "F1",
    "buildDispenser" -> "F2",
    "buildEntrance" -> "F3",
    "buildExit" -> "F4",
    "precEnabled" -> false,
    "precLog" -> 1,
    "precScreens" -> 1,
    "precDeleteUseless" -> 0,
    "precMinStreak" -> 4,
    "precKillDelay" -> 15,
    "precDir" -> "demos",
    "precMode" -> 2,
    "precMark" -> "PGUP")

  /*
   * Render the templates
   */
  def render: Map[String, String] = {
    configNames.map((configname: String) => {
      val result: String = engine.layout("templates" + File.separator + configname + ".cfg.ssp", Map(options.toList : _*) + (("currentConfig", configname)), List())
      (configname, result)
    }).toMap
  }

  /*
   * Write output templates to a directory
   */
  def writeToDirectory(directory: String) {
    render.foreach {
      case (filename, rendered) => {
        val out = new OutputStreamWriter(new FileOutputStream(directory + File.separator + filename + ".cfg"))
        out.write(rendered)
        out.close
      }
    }
  }
}