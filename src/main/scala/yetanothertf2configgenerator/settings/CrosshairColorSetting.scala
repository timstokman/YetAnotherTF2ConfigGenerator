package yetanothertf2configgenerator.settings

import scala.swing._
import event._
import Reactions.Reaction
import javax.swing.JColorChooser
import GridBagPanel._

case class CrosshairColorSetting(val name : String, val tf2Class : String, val weapon : Int, val settingType : Symbol) extends BaseSetting[(Int, Int, Int), GridBagPanel, List[TextField]] {
  val defaultValue = (255, 255, 255)
  val labelText = ""

  def getReaction(fire : () => Unit) = {
    {
      case ValueChanged(_) => fire()
      case _ => { }
    }
  }

  override def parseValue(guiStorage : List[TextField]) = (guiStorage(0).text.toInt, guiStorage(1).text.toInt, guiStorage(2).text.toInt)

  override def putValue(guiStorage : List[TextField], value : (Int, Int, Int)) {
    guiStorage(0).text = value._1.toString
    guiStorage(1).text = value._2.toString
    guiStorage(2).text = value._3.toString
  } 

  override def putReaction(reaction : Reaction, guiStorage : List[TextField]) {
    guiStorage(0).reactions += reaction
    guiStorage(1).reactions += reaction
    guiStorage(2).reactions += reaction
  }

  def pickColor(parent : Component, red : Int, green : Int, blue : Int) = {
    val color = new Color(red.toFloat / 255, green.toFloat / 255, blue.toFloat / 255)
    JColorChooser.showDialog(parent.peer, "Pick a crosshair color", color)
  }

  override def createGui(guiStorage : List[TextField]) = {
    new GridBagPanel {
      val c = new Constraints
      c.weightx = 0.5
      c.insets = new Insets(2, 2, 2, 2)
      c.anchor = Anchor.LineStart
      c.gridwidth = 2
      layout(errorLabel.getOrElse(Swing.HGlue)) = c
      c.gridy = 1
      c.gridwidth = 1
      
      val labels = List(
        new Label("Red"),
        new Label("Green"),
        new Label("Blue")
      )
  
      labels.zip(guiStorage).foreach(pair => {
        c.anchor = Anchor.LineStart
        layout(pair._1) = c
        c.gridx = 1
        pair._2.preferredSize = new Dimension(math.max(BaseSetting.minSize, pair._2.preferredSize.width), pair._2.preferredSize.height)
        c.anchor = Anchor.LineEnd
        layout(pair._2) = c
        c.gridy = c.gridy + 1
        c.gridx = 0
      })

      c.anchor = Anchor.LineStart
      layout(new Button("Pick color") {
        reactions += {
          case ButtonClicked(_) => {
            val newColor = pickColor(this, value._1, value._2, value._3)
            guiStorage(0).text = newColor.getRed.toString
            guiStorage(1).text = newColor.getGreen.toString
            guiStorage(2).text = newColor.getBlue.toString
          }
        }
      }) = c
    }
  }

  override def createGuiStorage = {
    List(
      new TextField(),
      new TextField(),
      new TextField()
    )
  }

  override def createLabel = None

  def validate = {
    def checkColor(textField : TextField) = {
      try {
        val colorValue = textField.text.toInt
        colorValue >= 0 && colorValue <= 255
      } catch {
        case _ : java.lang.NumberFormatException => false
      }
    }
    checkColor(GUIStorage(0)) && checkColor(GUIStorage(1)) && checkColor(GUIStorage(2))
  }

  override def errorMessage = "color values must each be a number from 0 to 255 (RGB format)"
}
