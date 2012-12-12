package yetanothertf2configgenerator.settings

import scala.swing._
import event._
import GridBagPanel._
import javax.swing.SwingUtilities

object BindSetting {
  implicit def fun2Run[T](x: => T) : Runnable = new Runnable() { def run = x }
  
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
    
  val keyToBindMap = Map(
    Key.Key0 -> "0", Key.Key1 -> "1", Key.Key2 -> "2", Key.Key3 -> "3", Key.Key4 -> "4", Key.Key5 -> "5", Key.Key6 -> "6", Key.Key7 -> "7", Key.Key8 -> "8", Key.Key9 -> "9",
    Key.A -> "a", Key.B -> "b", Key.C -> "c", Key.D -> "d", Key.E -> "e", Key.F -> "f", Key.G -> "g", Key.H -> "h", Key.I -> "i", Key.J -> "j", Key.I -> "i", Key.J -> "j", Key.K -> "k", Key.L -> "l", Key.M -> "m", Key.N -> "n", Key.O -> "o", Key.P -> "p", Key.Q -> "q", Key.R -> "r", Key.S -> "s", Key.T -> "t", Key.U -> "u", Key.V -> "v", Key.W -> "w", Key.X -> "x", Key.Y -> "y", Key.Z -> "z",
    Key.F1 -> "F1", Key.F2 -> "F2", Key.F3 -> "F3", Key.F4 -> "F4", Key.F5 -> "F5", Key.F6 -> "F6", Key.F7 -> "F7", Key.F8 -> "F8", Key.F9 -> "F9", Key.F10 -> "F10", Key.F11 -> "F11", Key.F12 -> "F12",
    Key.Period -> ".",
    Key.Slash -> "/",
    Key.BackSlash -> "\\",
    Key.Alt -> "ALT",
    Key.Shift -> "SHIFT",
    Key.Minus -> "-",
    Key.Quote -> "'",
    Key.BackQuote -> "`",
    Key.Comma -> ",",
    Key.ScrollLock -> "SCROLLLOCK",
    Key.Home -> "HOME",
    Key.Insert -> "INS",
    Key.BackSpace -> "BACKSPACE",
    Key.Tab -> "TAB",
    Key.Enter -> "ENTER",
    Key.Control -> "CTRL",
    Key.Space -> "SPACE",
    Key.Multiply -> "KP_MULTIPLY",
    Key.Plus -> "KP_PLUS",
    Key.Numpad0 -> "KP_INS",
    Key.Numpad1 -> "KP_END",
    Key.Numpad2 -> "KP_DOWNARROW",
    Key.Numpad3 -> "KP_PGDN",
    Key.Numpad4 -> "KP_LEFTARROW",
    Key.Numpad5 -> "KP_5",
    Key.Numpad6 -> "KP_RIGHTARROW",
    Key.Numpad7 -> "KP_HOME",
    Key.Numpad8 -> "KP_UPARROW",
    Key.Numpad9 -> "KP_PGUP",
    Key.Pause -> "PAUSE",
    Key.Escape -> "ESCAPE",
    Key.Right -> "RIGHTARROW",
    Key.Left -> "LEFTARROW",
    Key.Up -> "UPARROW",
    Key.Down -> "DOWNARROW",
    Key.PageDown -> "PGDN",
    Key.PageUp -> "PGUP",
    Key.Semicolon -> "SEMICOLON",
    Key.Delete -> "DEL",
    Key.End -> "END",
    Key.NumLock -> "NUMLOCK"
  )
  
  class KeyDialog(callback: String => Unit) extends Frame {
    title = "Key selection"
      
    val keyField = new TextField("Type a key on your keyboard (Mouse keys, tab and right control are excluded)")
        
    listenTo(keyField.keys)    
    contents = keyField
    
    reactions += {
      case KeyPressed(_, key, modifiers, _) => {
        callback(keyToBindMap.getOrElse(key, ""))
        close
      }
    }
  } 
}

case class BindSetting(val name : String, val labelText : String, val tf2Class : String) extends AbstractChoiceSetting(BindSetting.bindKeys) {
  val settingType = 'binds
  val weapon = 0
  val defaultValue = Some("nothing")
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
  
  override def createGui(guiStorage : ComboBox[String]) = {
    new GridBagPanel {
      val c = new Constraints
      c.weightx = 0.5
      c.insets = new Insets(2, 2, 2, 2)
      c.anchor = Anchor.LineStart
      c.gridwidth = 3
      layout(errorLabel.getOrElse(Swing.HGlue)) = c
      c.gridwidth = 1
      c.gridy = 1
      layout(label.getOrElse(Swing.HGlue)) = c
      c.weightx = 0
      c.gridx = 1
      c.anchor = Anchor.LineEnd      
      guiStorage.preferredSize = new Dimension(math.max(BaseSetting.minSize, guiStorage.preferredSize.width), guiStorage.preferredSize.height)
      layout(guiStorage) = c
      c.gridx = 2
      layout(new Button("Type Key") {
        reactions += {
          case ButtonClicked(_) => {
            val dialog = new BindSetting.KeyDialog(key => {
              if(key != "")
                value = key
            })
            dialog.pack
            dialog.centerOnScreen
            dialog.visible = true
          }
        }
      }) = c
    }
  }
}
