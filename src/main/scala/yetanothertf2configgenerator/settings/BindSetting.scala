package yetanothertf2configgenerator.settings

import scala.swing._
import event._

object BindSetting {
  private val binds = collection.mutable.Map[String, List[BindSetting]]()
  Setting.classesWithAny.foreach(cls => {
    binds(cls) = List[BindSetting]()
  })

  def +=(setting : BindSetting) {
    binds(setting.tf2Class) = setting :: binds(setting.tf2Class)
  }

  def conflicts(setting : BindSetting, newValue : String) : Traversable[BindSetting] = {
    if(newValue != "nothing") {
      (if(setting.tf2Class == "any") {
        binds.foldLeft(List[BindSetting]())((acc, kv) => kv._2 ::: acc)
      } else {
        binds(setting.tf2Class) ++ binds("any")
      }).filter(conflict => conflict.name != setting.name && conflict.parseValue(conflict.GUIStorage) == newValue)
    } else {
      List()
    }
  }

  //list of valid tf2 key codes
  val bindKeys = List(
    "nothing",
    "MWHEELUP",
    "MWHEELDOWN",
    "MOUSE1",
    "MOUSE2",
    "MOUSE3",
    "MOUSE4",
    "MOUSE5",
    "`", ".", ",", "-", "]", "'", "\\", "/",
    "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
    "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12",
    "SHIFT",
    "CTRL",
    "SPACE",
    "ALT",
    "RSHIFT",
    "RCTRL",
    "ENTER",
    "TAB",
    "BACKSPACE",
    "SEMICOLOR",
    "INS",
    "HOME",
    "SCROLLLOCK",
    "NUMLOCK",
    "END",
    "DEL",
    "SEMICOLON",
    "PGUP",
    "PGDN",
    "UPARROW",
    "DOWNARROW",
    "LEFTARROW",
    "RIGHTARROW",
    "ESCAPE", 
    "PAUSE",
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
}

case class BindSetting(val name : String, val labelText : String, val tf2Class : String) extends AbstractChoiceSetting(BindSetting.bindKeys) {
  val settingType = 'binds
  val weapon = 0
  val defaultValue = "none"
  var lastConflicts : Traversable[BindSetting] = List()

  BindSetting += this

  override def validate = {
    lastConflicts = BindSetting.conflicts(this, parseValue(GUIStorage))
    super.validate && lastConflicts.isEmpty
  }

  def revalidateOldConflicts {
    lastConflicts.foreach(c => { c.validateAndError })
  }

  override def onChange() {
    revalidateOldConflicts
    super.onChange
    revalidateOldConflicts
  }

  override def errorMessage = {
    if(lastConflicts.nonEmpty) {
      "There is a conflict with the following bind setting(s): \"" + 
      lastConflicts.map(setting => setting.labelText + "\" for class " + setting.tf2Class + " bound to " + setting.parseValue(setting.GUIStorage)).mkString(", ")
    } else {
      super.errorMessage
    }
  }
}
