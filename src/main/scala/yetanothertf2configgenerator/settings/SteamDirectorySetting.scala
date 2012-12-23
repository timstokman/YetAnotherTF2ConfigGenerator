package yetanothertf2configgenerator.settings

import scala.swing._
import event._
import Reactions.Reaction
import java.io.File
import GridBagPanel._

object SteamDirectorySetting {
  //just a "cheap" check to see if it's likely to be a steam directory
  val necessaryDirectories = List("steamapps", "steam")

  val possibleSteamDirectories = List("C:\\Program Files\\Steam", "C:\\Program Files (x86)\\Steam", "~/Library/Application Support/Steam", "~/.steam/root/")

  val steamDirectoryFirstGuess = possibleSteamDirectories.find(dir => validateDirectory(new File(dir)))
                  
  def validateDirectory(dir : File) = { 
    dir.exists && dir.listFiles.map(_.getName.toLowerCase).intersect(necessaryDirectories).size == necessaryDirectories.size 
  }
}

case class SteamDirectorySetting(val name : String, val settingType : Symbol) extends BaseSetting[String, GridBagPanel, TextField] {
  val tf2Class = "any"
  val weapon = 0
  val labelText = "Steam directory"
  val errorMessage = "Not a valid steam directory"
  val defaultValue = SteamDirectorySetting.steamDirectoryFirstGuess
  
  override def necessary = false
    
  def createGui(guiStorage : TextField) = {
    new GridBagPanel {
      val c = new Constraints
      c.weightx = 0.5
      c.insets = new Insets(2, 2, 2, 2)
      c.anchor = Anchor.LineStart
      c.gridwidth = 2
      layout(errorLabel.getOrElse(Swing.HGlue)) = c
      c.gridwidth = 1
      c.gridy = 1
      layout(label.getOrElse(Swing.HGlue)) = c
      c.gridx = 1
      c.anchor = Anchor.LineEnd
      guiStorage.preferredSize = new Dimension(math.max(BaseSetting.minSize, guiStorage.preferredSize.width), guiStorage.preferredSize.height)
      layout(guiStorage) = c
      c.gridx = 2
      val grid = this
      layout(new Button("Select directory") {
        reactions += {
          case ButtonClicked(_) => {
            val chooser = new FileChooser {
              fileSelectionMode = FileChooser.SelectionMode.DirectoriesOnly
            }
            if(chooser.showOpenDialog(grid) == FileChooser.Result.Approve)
              value = chooser.selectedFile.toString
          }
        }
      }) = c
    }
  }
  
  override def putReaction(reaction : Reaction, guiStorage : TextField) {
    guiStorage.reactions += reaction
  }
  
  def getReaction(fire : () => Unit) = {
    {
      case ValueChanged(_) => fire()
    }
  }
  
  def createGuiStorage = new TextField()
  
  def putValue(guiStorage : TextField, value : String) {
    guiStorage.text = value
  }
  
  def parseValue(guiStorage : TextField) = guiStorage.text
  
  def validate = SteamDirectorySetting.validateDirectory(new File(GUIStorage.text))
}
