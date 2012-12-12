package yetanothertf2configgenerator.settings

import scala.swing._
import event._
import java.awt.{AlphaComposite, image}
import image.BufferedImage
import javax.imageio.ImageIO
import java.io.File
import GridBagPanel._

object CrosshairSetting {
  val resourceDir = "resources"

  //list of crosshair types
  val crosshairTypes = List("\"\"", "crosshair1", "crosshair2", "crosshair3", "crosshair4", "crosshair5", "crosshair6", "crosshair7")

  private class ImagePanel(var imagePath : Option[String], var color : Option[Color]) extends Panel {                                                                             
    override def paintComponent(g:Graphics2D) = {                                                                           
      color.foreach(col => {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f))
        g.setPaint(col)
        g.setBackground(col)
        g.clearRect(0, 0, 64, 64)
      })
      imagePath.map(path => ImageIO.read(new File(path))).foreach(image => g.drawImage(image, 0, 0, null))
    }                                                                           
  }
}

case class CrosshairSetting(val name : String, val labelText : String, val tf2Class : String, val weapon : Int) extends AbstractChoiceSetting(CrosshairSetting.crosshairTypes) {
  val crosshairImageSize = 64
  val settingType = 'options
  val defaultValue = Some("\"\"")
  override val canSubscribe = true
  private val imagePanel = new CrosshairSetting.ImagePanel(None, Some(new Color(1.0f, 1.0f, 1.0f))) { this.preferredSize = new Dimension(crosshairImageSize, crosshairImageSize) }

  override def createGui(guiStorage : ComboBox[String]) = {
    new GridBagPanel {
      val c = new Constraints
      c.weightx = 0.5
      c.insets = new Insets(2, 2, 2, 2)
      c.anchor = Anchor.LineStart
      c.gridwidth = 2
      layout(errorLabel.getOrElse(Swing.HGlue)) = c
      c.gridy = 1
      c.anchor = Anchor.LineEnd
      layout(imagePanel) = c
      c.anchor = Anchor.LineStart
      c.gridwidth = 1
      c.gridy = 2
      layout(label.getOrElse(Swing.HGlue)) = c
      c.gridx = 1
      c.anchor = Anchor.LineEnd
      guiStorage.preferredSize = new Dimension(math.max(BaseSetting.minSize, guiStorage.preferredSize.width), guiStorage.preferredSize.height)
      layout(guiStorage) = c
    }
  }

  override def onChange {
    super.onChange
    if(value == "\"\"")
      imagePanel.imagePath = None
    else
      imagePanel.imagePath = Some(CrosshairSetting.resourceDir + File.separator + value + ".png")
    imagePanel.repaint
  }

  override def canSubscribeTo(setting : Setting[_, _ <: Component]) = {
    setting match {
      case CrosshairColorSetting(_, colorTf2Class, colorWeapon, _) => colorTf2Class == tf2Class && colorWeapon == weapon
      case _ => false
    }
  }

  override def notify(publisher : Setting[_, _ <: Component], event : SettingEvent[_, _ <: Component]) { 
    (event: @unchecked) match {
      case SettingChanged(_, newValue : (Int, Int, Int)) => {
        imagePanel.color = Some(new Color(newValue._1 / 255.0f, newValue._2 / 255.0f, newValue._3 / 255.0f))
        imagePanel.repaint
      }
      case _ => { }
    }
  }
}
