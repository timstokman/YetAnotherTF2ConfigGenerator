package yetanothertf2configgenerator.settings

import scala.swing._
import event._
import collection.mutable.{Publisher, Subscriber}
import collection.immutable.ListMap

object Setting {
  /*
   * I'd rather not have this function but..
   * * Scala has no proper implementation of mapValues in ListedMap that also returns a ListedMap
   * * Scala has no listed group by (would be nice to have, probably a bit too specific for a library though)
   */
  private def listedGroupBy[A, Repr <: Traversable[A], K, C](list : Repr, getKey : A => K, mValues : List[A] => C) : ListMap[K, C] = {
    val map = collection.mutable.ListMap[K, collection.mutable.Buffer[A]]()
    list.foreach(el => {
      val key = getKey(el)
      val builder = map.getOrElseUpdate(key, collection.mutable.Buffer[A]())
      builder += el
    })
    ListMap(map.map(pair => (pair._1, mValues(pair._2.toList))).toSeq : _*)
  }

  def wireSubscribers {
    expandedSettings.foreach(setting => {
      if(setting.canSubscribe) {
        expandedSettings.foreach(to => {
          if(setting.canSubscribeTo(to))
            to.subscribe(setting.asInstanceOf[to.Sub])
        })
      }
    })
  }

  private def expandSettings(settings : List[Setting[_, _ <: Component]]) : List[Setting[_, _ <: Component]] = {
    var newSettings = List[Setting[_, _ <: Component]]()
    var dependencySettings = List[Setting[_, _ <: Component]]()
    settings.foreach {
      case s : DependencySetting[_, Component, Traversable[_], _] => {
        newSettings = newSettings ::: expandSettings(s.expandedSettings.toList)
      }
      case s : Setting[_, Component] => {
        newSettings = newSettings :+ s
      }
    }
    dependencySettings ::: newSettings
  }

  //group settings by settingtype/class/weapon
  def groupSettings(settings : List[Setting[_, _ <: Component]]) = listedGroupBy(settings, (s : Setting[_, _ <: Component]) => s.settingType, (classList : List[Setting[_, _ <: Component]]) => 
                                                                                 listedGroupBy(classList, (s : Setting[_, _ <: Component]) => s.tf2Class, (weaponList : List[Setting[_, _ <: Component]]) => 
                                                                                               listedGroupBy(weaponList, (s : Setting[_, _ <: Component]) => s.weapon, (s : List[Setting[_, _ <: Component]]) => s)))

  def getTemplateData = settings.map(s => (s.name, s.value)).toMap

  def getTemplateVariableDeclarations = {
    (settings.map(_.templateVariableDeclaration) :+ "<%@ val currentConfig: String %>" :+ "#import(yetanothertf2configgenerator.ConfigGenerator._)" :+ "").mkString(sys.props("line.separator"))
  }

  /*
   * backwards compatibility for older versions
   *
   * I'll probably take this out after a couple of versions, bit messy
   */
  def backwardsCompatibilityFilter(name : String, value : Any, restProfile : Map[String, _]) = {
    val fixedValue = value match {
      case value : collection.mutable.HashMap[_, _] => value.asInstanceOf[collection.mutable.HashMap[_, _]].toMap
      case _ => value
    }
    name match {
      case "classSensitivity" => ("classWeaponSensitivity", fixedValue)
      case "classDingPitchMax" => ("classWeaponDingPitchMax", fixedValue)
      case "classDingPitchMin" => ("classWeaponDingPitchMin", fixedValue)
      case "classDingVolume" => ("classWeaponDingVolume", fixedValue)
      case "classInterpRatio" => ("classWeaponInterpRatio", fixedValue)
      case "weaponShow" => ("classWeaponShow", classes.map(cls => (cls, fixedValue.asInstanceOf[List[Boolean]])).toMap ++ restProfile("classWeaponShow").asInstanceOf[Map[String, List[Boolean]]])
      case "weaponScales" => ("classWeaponScales", classes.map(cls => (cls, fixedValue.asInstanceOf[List[Int]])).toMap ++ restProfile("classWeaponScales").asInstanceOf[Map[String, List[Int]]])
      case "weaponCrosshairs" => ("classWeaponCrosshairs", classes.map(cls => (cls, fixedValue.asInstanceOf[List[String]])).toMap ++ restProfile("classWeaponCrosshairs").asInstanceOf[Map[String, List[Int]]])
      case "weaponColors" => ("classWeaponColors", classes.map(cls => (cls, fixedValue.asInstanceOf[List[(Int, Int, Int)]])).toMap ++ restProfile("classWeaponColors").asInstanceOf[Map[String, (Int, Int, Int)]])
      case "crouchJumpNotFor" => ("classEnableCrouchJump", classes.map(cls => (cls, !fixedValue.asInstanceOf[List[String]].contains(cls))).toMap)
      case _ => (name, fixedValue)
    }
  }

  def applyProfile(profile : Map[String, _]) {
    profile.foreach((pair: (String, Any)) => {
      val (name, value) = backwardsCompatibilityFilter(pair._1, pair._2, profile)
      settings.find(_.name == name) match {
        case Some(setting) => setting.asInstanceOf[Setting[Any, _]].value = value
        case None => settings.find(_.innerName == name).foreach(_.asInstanceOf[DependencySetting[Any, _, _, _]].setInnerValue(value))
      }
    })
  }

  def getSettingByName(name : String) = settings.find(_.name == name)

  def getMessages = {
    settings.flatMap(_.extraMessage)
  }

  //list of classes
  val classes = List(
    "scout",
    "soldier",
    "pyro",
    "demoman",
    "heavyweapons",
    "engineer",
    "medic",
    "sniper",
    "spy")
  
  val classesWithAny = "any" :: classes

  //list of options
  val options = 0 to 4

  //complete list of all the settings
  lazy val settings = List[Setting[_, _ <: Component]](
    SteamDirectorySetting("steamDir", 'options),
    SteamUsernameSetting("steamUser", 'options),

    ClassDependencySetting(tf2Class => WeaponDependencySetting(weapon => DoubleSetting("sensitivity", "Mouse sensitivity", tf2Class, weapon, Some(1.0), 'options, Some(0.0), None), true), true),
    ClassDependencySetting(tf2Class => WeaponDependencySetting(weapon => ViewmodelSwitchmodeSetting("viewmodelSwitchMode", "Viewmodel switch mode", tf2Class, weapon), true), true),
    ClassDependencySetting(tf2Class => WeaponDependencySetting(weapon => BooleanSetting("show", "Always show weapon", tf2Class, weapon, Some(false), 'options), false), true),
    ClassDependencySetting(tf2Class => WeaponDependencySetting(weapon => IntAsBooleanSetting("textBatch", "Batch damage numbers", tf2Class, weapon, Some(0), 'options), true), true),
    ClassDependencySetting(tf2Class => WeaponDependencySetting(weapon => CrosshairSetting("crosshairs", "Crosshair type", tf2Class, weapon), false), true),
    ClassDependencySetting(tf2Class => WeaponDependencySetting(weapon => CrosshairColorSetting("colors", tf2Class, weapon, 'options), false), true),
    ClassDependencySetting(tf2Class => WeaponDependencySetting(weapon => IntSetting("scales", "Crosshair scale", tf2Class, weapon, Some(24), 'options, Some(0), None), false), true),
    ClassDependencySetting(tf2Class => WeaponDependencySetting(weapon => DoubleSetting("dingVolume", "Dingaling volume", tf2Class, weapon, Some(1.0), 'options, Some(0.0), None), false), true),
    ClassDependencySetting(tf2Class => WeaponDependencySetting(weapon => IntSetting("dingPitchMax", "Dingaling pitch max", tf2Class, weapon, Some(50), 'options, Some(0), None), false), true),
    ClassDependencySetting(tf2Class => WeaponDependencySetting(weapon => IntSetting("dingPitchMin", "Dingaling pitch max", tf2Class, weapon, Some(100), 'options, Some(0), None), false), true),
    ClassDependencySetting(tf2Class => BooleanSetting("enableCrouchJump", "Enable crouch-jump", tf2Class, 0, Some(false), 'options), true),
    ClassDependencySetting(tf2Class => DoubleSetting("interpRatio", "Interp-ratio: ", tf2Class, 0, Some(1.0), 'options, Some(0.0), None), false),

    IntAsBooleanSetting("dingEnable", "Enable weapon dingalings", "any", 0, Some(1), 'options),
    IntAsBooleanSetting("damagenumbers", "Enable damage numbers", "any", 0, Some(1), 'options),
    BooleanSetting("hideTracers", "Hide hitscan tracers", "any", 0, Some(true), 'options),
    BooleanSetting("enableSprays", "Enable sprays", "any", 0, Some(true), 'options),
    BooleanSetting("enableGibs", "Enable gibs", "any", 0, Some(true), 'options),
    BooleanSetting("enableRagdolls", "Enable ragdolls", "any", 0, Some(true), 'options),
    IntAsBooleanSetting("autoreload", "Enable autoreload", "any", 0, Some(1), 'options),
    IntSetting("fov", "Field of view", "any", 0, Some(90), 'options, Some(70), Some(90)),
    IntAsBooleanSetting("fastswitch", "Enable weapon fastswitch", "any", 0, Some(1), 'options),
    IntSetting("viewmodelFov", "Viewmodel field of view", "any", 0, Some(80), 'options, Some(0), None),
    NetworkConfigSetting("networkConfig", "Include network configuration:"),
    GraphicsConfigSetting("graphicsConfig", "Include graphics configuration:"),
    DoubleSetting("zoomFactor", "Zoom factor", "any", 0, Some(1.2), 'options, Some(0.0), Some(1.2)),
    IntSetting("scoreboardNetgraph", "Netgraph type when scoreboard pops up", "any", 0, Some(0), 'options, Some(0), Some(4)),
    BooleanSetting("precEnabled", "I use P-Rec", "any", 0, Some(false), 'options),
    IntSetting("precLog", "P-Rec log mode", "any", 0, Some(0), 'options, Some(0), Some(4)),
    IntAsBooleanSetting("precScreens", "P-Rec take screenshots of scoreboard/status", "any", 0, Some(0), 'options),
    IntAsBooleanSetting("precDeleteUseless", "P-Rec delete useless demos w/o bookmarks/killstreaks", "any", 0, Some(0), 'options),
    IntSetting("precMinStreak", "P-Rec minimum killstreak", "any", 0, Some(4), 'options, Some(0), None),
    IntSetting("precKillDelay", "P-Rec killstreak delay", "any", 0, Some(15), 'options, Some(0), None),
    StringSetting("precDir", "P-Rec demo directory", "any", 0, None, 'options),
    IntSetting("precMode", "P-Rec mode", "any", 0, Some(0), 'options, Some(0), None),
    IntSetting("autocallThreshold", "Autocall threshold", "medic", 0, Some(75), 'options, Some(0), None),
    BooleanSetting("showHealBeam", "Show heal beam", "medic", 0, Some(true), 'options),
    IntAsBooleanSetting("healBatching", "Batch heal text", "medic", 0, Some(0), 'options),
    StringSetting("medicSayFake", "Chat on uber faked", "medic", 0, None, 'options),
    StringSetting("medicSayUber", "Chat on uber popped", "medic", 0, None, 'options),
    DoubleSetting("zoomSensitivity", "Zoom sensitivity", "sniper", 0, Some(1.0), 'options, Some(0.0), None),
    IntAsBooleanSetting("noCrossHairZoom", "No crosshair zoom", "sniper", 0, Some(0), 'options),
    IntAsBooleanSetting("fullChargeBell", "Full charge bell", "sniper", 0, Some(1), 'options),
    BooleanSetting("showFlames", "Show flames", "pyro", 0, Some(true), 'options),
    BooleanSetting("showViewmodelCloak", "Show viewmodel on cloak/DR", "spy", 0, Some(true), 'options),
    ClassAsIntListSetting("disguiseCycleClasses", "spy", 0, 'options, "Disguise cycle list"),
    ClassAsIntListSetting("disguiseFriendlyCycleClasses", "spy", 0, 'options, "Friendly disguise cycle list"),
    IntAsBooleanSetting("enableSpecTournamentMode", "Enable spectator tournament mode", "any", 0, Some(1), 'options),
    ClassDependencySetting(tf2Class => TextareaSetting("extraScripts", "Additional script", tf2Class, 0, None, 'options), false),
    TextareaSetting("autoexecExtraScript", "Additional script", "any", 0, None, 'options),

    BindSetting("movementFwd", "Move forward", "any"),
    BindSetting("movementLeft", "Move left", "any"),
    BindSetting("movementRight", "Move right", "any"),
    BindSetting("movementBack", "Move back", "any"),
    BindSetting("primAttack", "Primary attack", "any"),
    BindSetting("secAttack", "Secondary attack", "any"),
    BindSetting("primSecSwap", "Swap primary secondary weapon", "any"),
    BindSetting("meleeSwap", "Swap current and melee weapon (and back)", "any"),
    BindSetting("previousWeapon", "Swap to previous weapon", "any"),
    BindSetting("nextInvWeapon", "Swap to next weapon inventory order", "any"),
    BindSetting("prevInvWeapon", "Swap to previous weapon inventory order", "any"),
    BindSetting("directWeapon1", "Swap to primary weapon", "any"),
    BindSetting("directWeapon2", "Swap to secondary weapon", "any"),
    BindSetting("directWeapon3", "Swap to melee weapon", "any"),
    BindSetting("directWeapon4", "Swap to PDA/Watch", "any"),
    BindSetting("voiceMenu1", "Voice menu 1", "any"),
    BindSetting("voiceMenu2", "Voice menu 2", "any"),
    BindSetting("voiceMenu3", "Voice menu 3", "any"),
    BindSetting("actionSlot", "Action slot", "any"),
    BindSetting("taunt", "Taunt", "any"),
    BindSetting("dropItem", "Drop Item", "any"),
    BindSetting("inspect", "Inspect", "any"),
    BindSetting("say", "Chat", "any"),
    BindSetting("teamSay", "Team chat", "any"),
    BindSetting("console", "Console", "any"),
    BindSetting("duck", "Duck", "any"),
    BindSetting("reloadhud", "Reload hud/fix glitches", "any"),
    BindSetting("suicide", "Kill yourself", "any"),
    BindSetting("screenshot", "Take screenshot", "any"),
    BindSetting("saveReplay", "Save replay", "any"),
    BindSetting("quickSwitch", "Quickly switch item inventory", "any"),
    BindSetting("teamOnlyTalk", "Team-only voice", "any"),
    BindSetting("normalTalk", "Normal voice", "any"),
    BindSetting("medic", "Call medic", "any"),
    BindSetting("scoreboard", "Show scoreboard", "any"),
    BindSetting("backpack", "Show backpack", "any"),
    BindSetting("switchClass", "Switch classes", "any"),
    BindSetting("switchTeam", "Switch teams", "any"),
    BindSetting("medicRadar", "Medic radar", "medic"),
    BindSetting("jump", "Jump", "any"),
    BindSetting("normalJump", "Jump always without crouch", "any"),
    BindSetting("zoom", "Zoom in", "any"),
    BindSetting("spray", "Spray on surface", "any"),
    BindSetting("requestAccept", "Accept notification", "any"),
    BindSetting("requestDecline", "Decline notification", "any"),
    BindSetting("switchBadnet", "Switch to a bad-network configuration", "any"),
    BindSetting("switchGoodnet", "Switch to a good-network configuration", "any"),
    BindSetting("switchLannet", "Switch to a lan-network configuration", "any"),
    BindSetting("startSpawnSwitch", "Start switching spawns by joining a random class", "any"),
    BindSetting("finishSpawnSwitch", "Finish switch spawns by joining your original class", "any"),
    BindSetting("disguiseMenuToggle", "Toggle disguises within disguise menu", "spy"),
    BindSetting("disguiseCycle", "Cycle normal disguise", "spy"),
    BindSetting("disguiseFriendlyCycle", "Cycle friendly disguise", "spy"),
    BindSetting("disguiseLast", "Last disguise", "spy"),
    BindSetting("disguiseScout", "Disguise scout", "spy"),
    BindSetting("disguiseSoldier", "Disguise soldier", "spy"),
    BindSetting("disguisePyro", "Disguise pyro", "spy"),
    BindSetting("disguiseDemo", "Disguise demo", "spy"),
    BindSetting("disguiseHeavy", "Disguise heavy", "spy"),
    BindSetting("disguiseEngineer", "Disguise engineer", "spy"),
    BindSetting("disguiseMedic", "Disguise medic", "spy"),
    BindSetting("disguiseSniper", "Disguise sniper", "spy"),
    BindSetting("disguiseSpy", "Disguise spy", "spy"),
    BindSetting("disguiseFriendlyScout", "Disguise friendly scout", "spy"),
    BindSetting("disguiseFriendlySoldier", "Disguise friendly soldier", "spy"),
    BindSetting("disguiseFriendlyPyro", "Disguise friendly pyro", "spy"),
    BindSetting("disguiseFriendlyDemo", "Disguise friendly demo", "spy"),
    BindSetting("disguiseFriendlyHeavy", "Disguise friendly heavy", "spy"),
    BindSetting("disguiseFriendlyEngineer", "Disguise friendly engineer", "spy"),
    BindSetting("disguiseFriendlyMedic", "Disguise friendly medic", "spy"),
    BindSetting("disguiseFriendlySniper", "Disguise friendly sniper", "spy"),
    BindSetting("disguiseFriendlySpy", "Disguise friendly spy", "spy"),
    BindSetting("buildSentry", "Destroy and build new sentry", "engineer"),
    BindSetting("buildDispenser", "Destroy and build new dispenser", "engineer"),
    BindSetting("buildEntrance", "Destroy and build new entrance", "engineer"),
    BindSetting("buildExit", "Destroy and build new exit", "engineer"),
    BindSetting("destroySentry", "Destroy sentry", "engineer"),
    BindSetting("destroyDispenser", "Destroy dispenser", "engineer"),
    BindSetting("destroyEntrance", "Destroy entrance", "engineer"),
    BindSetting("destroyExit", "Destroy exit", "engineer"),
    BindSetting("switchSpec", "Switch spectator target", "any"),
    BindSetting("precMark", "P-Rec mark", "any"),
    BindSetting("toggleReady", "Toggle ready", "any"),
    BindSetting("voice01", "Voice Thanks", "any"),
    BindSetting("voice02", "Voice Go", "any"),
    BindSetting("voice03", "Voice Move Up", "any"),
    BindSetting("voice04", "Voice Flank Left", "any"),
    BindSetting("voice05", "Voice Flank Right", "any"),
    BindSetting("voice06", "Voice Yes", "any"),
    BindSetting("voice07", "Voice No", "any"),
    BindSetting("voice10", "Voice Incoming", "any"),
    BindSetting("voice11", "Voice Cloaked Spy", "any"),
    BindSetting("voice12", "Voice Sentry Ahead", "any"),
    BindSetting("voice13", "Voice Teleporter Here", "any"),
    BindSetting("voice14", "Voice Dispenser Here", "any"),
    BindSetting("voice15", "Voice Sentry Here", "any"),
    BindSetting("voice16", "Voice Activate Charge", "any"),
    BindSetting("voice17", "Voice Charge Ready", "medic"),
    BindSetting("voice20", "Voice Help", "any"),
    BindSetting("voice21", "Voice Battlecry", "any"),
    BindSetting("voice22", "Voice Cheers", "any"),
    BindSetting("voice23", "Voice Jeers", "any"),
    BindSetting("voice24", "Voice Positive", "any"),
    BindSetting("voice25", "Voice Negative", "any"),
    BindSetting("voice26", "Voice Nice Shot", "any"),
    BindSetting("voice27", "Voice Good Job", "any")
  )

  //group settings, helper for gui layout
  lazy val expandedSettings = expandSettings(settings)
  lazy val groupedSettings = groupSettings(expandedSettings)
  lazy val templateVariableDeclarations = getTemplateVariableDeclarations
}

abstract class Setting[ValueType, GUIType <: Component] extends Publisher[SettingEvent[_, _]] with Subscriber[SettingEvent[_, _ <: Component], Setting[_, _ <: Component]]  {
  var value : ValueType

  def valueClass : Class[_]
  def name : String
  def settingType : Symbol
  def tf2Class : String
  def weapon : Int
  def GUI : GUIType
  def extraMessage : Option[String] = None
  def label : Option[Label]
  def errorLabel : Option[Label]
  def templateVariableDeclaration : String 
  def innerName : String
  def validate : Boolean
  def validateAndError : Boolean
  def canSubscribe : Boolean
  def canSubscribeTo(setting : Setting[_, _ <: Component]) : Boolean
}
