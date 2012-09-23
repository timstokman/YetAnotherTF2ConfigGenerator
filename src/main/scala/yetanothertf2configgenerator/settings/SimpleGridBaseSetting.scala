package yetanothertf2configgenerator.settings

import scala.swing._
import event._
import Reactions.Reaction
import GridBagPanel._
import java.awt.Insets

abstract class SimpleGridBaseSetting[ValueType, GUIStorage <: Component]()(implicit val valueManifest : Manifest[ValueType]) extends BaseSetting[ValueType, GridBagPanel, GUIStorage]()(valueManifest) {

  override def putReaction(reaction : Reaction, guiStorage : GUIStorage) {
    guiStorage.reactions += reaction
  }

  override def createGui(guiStorage : GUIStorage) = {
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
    }
  }
}
