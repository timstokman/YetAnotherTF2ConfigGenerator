package yetanothertf2configgenerator.settings

import scala.swing._
import event._
import Reactions.Reaction
import GridBagPanel._
import Swing._

abstract class ListSetting[ContainedType](val displayMap : Map[ContainedType, String])(implicit valueManifest : Manifest[List[ContainedType]]) extends BaseSetting[List[ContainedType], GridBagPanel, ListView[ContainedType]]()(valueManifest) {
  val defaultValue = Some(List[ContainedType]())
  val itemValues = displayMap.keys.toSeq
  val listRenderer = ListView.Renderer[ContainedType, String](v => displayMap(v))
  val listMinimumSize = new Dimension(120, 140)

  def errorMessage = "Items must be one of " + displayMap.values.mkString(", ")

  def createGuiStorage = new ListView[ContainedType](defaultValue.getOrElse(List())) {
    renderer = listRenderer
    border = EtchedBorder(Raised)
    preferredSize = listMinimumSize
  }

  def createGui(guiStorage : ListView[ContainedType]) = {
    new GridBagPanel {
      val c = new Constraints
      c.weightx = 0.5
      c.insets = new Insets(2, 2, 2, 2)
      c.anchor = Anchor.LineStart
      c.gridwidth = 2
      layout(errorLabel.getOrElse(Swing.HGlue)) = c
      c.gridy = 1
      layout(label.getOrElse(Swing.HGlue)) = c
      c.gridwidth = 1
      c.gridheight = 4
      c.gridy = 2
      layout(guiStorage) = c
      c.anchor = Anchor.LineEnd
      c.gridheight = 1
      c.gridx = 1
      val selectItems = new ComboBox(itemValues) {
        renderer = listRenderer
      }
      layout(selectItems) = c
      c.gridy = 3
      layout(new Button("Add item") {
        reactions += {
          case ButtonClicked(_) => guiStorage.listData = guiStorage.listData :+ selectItems.selection.item
        }
      }) = c
      c.gridy = 4
      layout(new Button("Remove items") {
        reactions += {
          case ButtonClicked(_) => {
            val newList = guiStorage.listData.toBuffer
            guiStorage.selection.indices.toSeq.sortBy(-1 * _).foreach(i => newList.remove(i))
            guiStorage.listData = newList
          }
        }
      }) = c
      c.gridy = 5
      layout(Swing.VGlue) = c
    }
  }

  def getReaction(fire : () => Unit) = {
    {
      case ListChanged(_) => fire()
      case ListElementsRemoved(_, _) => fire()
      case ListElementsAdded(_, _) => fire()
    }
  }

  def putReaction(reaction : Reaction, guiStorage : ListView[ContainedType]) {
    guiStorage.reactions += reaction
  }

  def parseValue(guiStorage : ListView[ContainedType]) = {
    guiStorage.listData.toList
  }
  
  def putValue(guiStorage : ListView[ContainedType], value : List[ContainedType]) {
    guiStorage.listData = value.toSeq
  }

  def validate = GUIStorage.listData.forall(item => itemValues.contains(item))
}
