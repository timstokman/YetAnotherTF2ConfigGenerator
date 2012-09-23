package yetanothertf2configgenerator.settings

import scala.swing._
import event._
import java.io.File
import javax.swing.{JComboBox, DefaultComboBoxModel}

object SteamUsernameSetting {
  val commonDirs = List("common", "downloading", "sourcemods", "temp")

  def getUsernames(steamDirectory : File) = steamDirectory.listFiles.filter(_.getName == "steamapps")(0).listFiles.filter(file => !commonDirs.exists(_ == file.getName) && file.isDirectory).map(_.getName).toList
}

case class SteamUsernameSetting(val name : String, val settingType : Symbol) extends AbstractChoiceSetting(List()) {
  val defaultValue = ""
  val labelText = "Username"
  val weapon = 0
  val tf2Class = "any"
  override val canSubscribe = true

  override def createGuiStorage = new ComboBox[String](SteamDirectorySetting.steamDirectoryFirstGuess.map(dir => SteamUsernameSetting.getUsernames(new File(dir))).getOrElse(List[String]())) {
    peer.setModel(new javax.swing.DefaultComboBoxModel)
  }

  override def canSubscribeTo(setting : Setting[_, _]) = {
    setting match {
      case s : SteamDirectorySetting => {
        true
      }

      case _ => { false }
    }
  }

  override def notify(publisher : Setting[_, _], event : SettingEvent[_, _]) {
    event match {
      case SettingChanged(oldValue : String, newValue : String) => {
        val steamDir = new File(newValue)
        val usernames = SteamUsernameSetting.getUsernames(steamDir)
        choices = usernames
        GUIStorage.peer.removeAllItems
        usernames.foreach(name => GUIStorage.peer.addItem(name))
        value = usernames.head
      }
      case _ => { }
    }
  }
}
