package yetanothertf2configgenerator

import scala.swing._
import event._
import Swing._
import javax.swing.{UIManager, JFrame}
import TabbedPane._
import GridBagPanel._
import scala.collection.immutable.ListMap
import settings._
import java.io.{File, FileOutputStream, FileInputStream}
import java.nio.ByteBuffer
import javax.swing.JOptionPane
import java.awt.Desktop
import java.net.URI
import scala.util.matching.Regex

object GUIRunner extends SwingApplication {
  val profileDir = "profiles"
  val helpURI = new URI("https://github.com/logophobia/YetAnotherTF2ConfigGenerator#faq")
  val aboutURI = new URI("https://github.com/logophobia/YetAnotherTF2ConfigGenerator#yet-another-tf2-config-generator")
  val configfileRegex = """.*\.cfg$"""

  override def startup(args: Array[String]) {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
    topFrame.pack
    topFrame.visible = true
    topFrame.maximize
  }

  override def shutdown {
    saveProfile
    super.shutdown
  }

  def generateTabsGroupedSettings[A, T](settings : ListMap[A, T], default : A, viablePath : T => Boolean, attach : Component => Unit, tabName : A => String, iterate : (T, Component => Unit) => Unit) {
    if(settings.get(default).map(setting => viablePath(setting)).getOrElse(false) && settings.keys.filterNot(_ == default).forall(k => settings.get(k).map(setting => !viablePath(setting)).getOrElse(true))) {
      iterate(settings(default), attach)
    } else {
      attach(new TabbedPane {
        settings.keys.foreach(a => {
          val setting = settings(a)
          val page = new Page(tabName(a), null)
          iterate(setting, c => { page.content = c })
	  pages += page
        })
      })
    }
  }

  def fillProfileMenu(menu : Menu) {
    menu.contents.clear
    val profileFile = """^(.*)\.xml$""".r
    new File(profileDir).listFiles.foreach(file => {
      file.getName match {
	case profileFile(profileName) => {
	  menu.contents += new MenuItem(Action(profileName) {
	    try {
	      Setting.applyProfile(ProfileManager.readProfile(profileName))
	    } catch {
	      case e : Exception => {
		handleException(e)
	      }
	    }
	  })
	}
	case _ =>
      }
    })
  }

  def progressWindow = new Dialog {
    title = "Generating"
    contents = new FlowPanel {
      contents += new ProgressBar {
	indeterminate = true
      }
    }
  }

  def getTF2Directory = {
    Setting.getSettingByName("steamDir").flatMap(dir => {
      Setting.getSettingByName("steamUser").flatMap(user => {
	println("has user and dir")
	if(dir.validateAndError && user.validateAndError) {
	  val cfgDir = new File(List(dir.value, "steamapps", user.value, "team fortress 2", "tf", "cfg").mkString(File.separator))
	  println("cfgs: " + cfgDir.getPath + " " + cfgDir.exists)
	  if(cfgDir.exists)
	    Some(cfgDir)
	  else
	    None
	} else {
	  None
	}
      })
    })
  }

  def showMessages {
    (Setting.getMessages :+ "Done generating configuration files").foreach(message => {
      Dialog.showMessage(null, message, "Info", Dialog.Message.Info)
    })
  }

  def saveConfigsTo(dir : File) {
    val progress = progressWindow
    try {
      progress.open
      new Thread {
        ConfigGenerator.writeToDirectory(dir.getAbsolutePath)
        progress.close
        showMessages
      }.start
    } catch {
      case e : Exception => {
        progress.close
	handleException(e)
      }
    }
  }

  def handleException(e : Exception) {
    val message = "Exception: " + e.getMessage
    val longMessage = message + "\n" + e.getStackTrace.map(_.toString).mkString("\n")
    Dialog.showMessage(null, message, "Error", Dialog.Message.Error)
    Console.err.println(longMessage)
  }

  def saveProfile {
    Option(JOptionPane.showInputDialog("Save settings to profile")).foreach(profileName => {
      try {
        ProfileManager.writeProfile(profileName, Setting.getTemplateData)
      } catch {
	case e : Exception => {
	  handleException(e)
	}
      }
    })
  }

  def pickDir = {
    val chooser = new FileChooser {
      fileSelectionMode = FileChooser.SelectionMode.DirectoriesOnly
    }
    if(chooser.showOpenDialog(null) == FileChooser.Result.Approve && chooser.selectedFile.exists)
      Some(chooser.selectedFile)
    else
      None
  }

  def tf2DirOrError(action : File => Unit) {
    getTF2Directory match {
      case Some(dir : File) => action(dir)
      case None => Dialog.showMessage(null, "No valid steam directory or username set", "Error", Dialog.Message.Error)
    } 
  }

  def copyDirFiles(from : File, to : File, pattern : String) {
    try {
      from.listFiles.foreach(fromFile => {
        if(fromFile.getName.matches(pattern)) {
	  val fromStream = new FileInputStream(fromFile)
	  val toStream = new FileOutputStream(new File(to.getPath + File.separator + fromFile.getName))
          val fromChannel = fromStream.getChannel
          val toChannel = toStream.getChannel
	  try {
	    fromChannel.transferTo(0, Long.MaxValue, toChannel)
	  } finally {
	    fromStream.close
	    toStream.close
	    fromChannel.close
	    toChannel.close
	  }
        }
      })
    } catch {
      case e : Exception => handleException(e)
    }
  }

  def createMenuBar : MenuBar = new MenuBar {
    contents += new Menu("File") {
      contents += new MenuItem(Action("Save configs to steam directory") {
	tf2DirOrError(dir => saveConfigsTo(dir))
      })
      contents += new MenuItem(Action("Save configs to") {
	pickDir.foreach(dir => saveConfigsTo(dir))
      })
      contents += new MenuItem(Action("Save settings") {
	saveProfile
	topFrame.menuBar = createMenuBar
      })
      contents += new MenuItem(Action("Quit") {
	quit
      })
    }
    contents += new Menu("Profiles") {
      fillProfileMenu(this)
    }
    contents += new Menu("Backup") {
      contents += new MenuItem(Action("Backup config files") {
	tf2DirOrError(steamDir => {
	  pickDir.foreach(backupDir => {
            copyDirFiles(steamDir, backupDir, configfileRegex)
	  })
	})
      })
      contents += new MenuItem(Action("Restore config files") {
	tf2DirOrError(steamDir => {
	  pickDir.foreach(backupDir => {
	    copyDirFiles(backupDir, steamDir, configfileRegex)
	  })
	})
      })
    }
    contents += new Menu("Help") {
      contents += new MenuItem(Action("Help") {
        Desktop.getDesktop.browse(helpURI)
      })
      contents += new MenuItem(Action("About") {
	Desktop.getDesktop.browse(aboutURI)
      })
    }
  }

  lazy val topFrame = new MainFrame {
    Setting.wireSubscribers
    ConfigGenerator.generateTemplatesWithVariableDeclarations
    title = "Yet Another TF2 Config Generator"
    menuBar = createMenuBar
    override def closeOperation() {
      quit
    }
    generateTabsGroupedSettings[Symbol, ListMap[String, ListMap[Int, List[Setting[_, _]]]]](Setting.groupedSettings, 'options, setting => true, c => this.contents = c,settingType => settingType.name.capitalize, (classMap, classAttach) => {
      generateTabsGroupedSettings[String, ListMap[Int, List[Setting[_, _]]]](classMap, "any", weaponMap => weaponMap.values.flatten.nonEmpty, classAttach, className => className.capitalize, (weaponMap, weaponAttach) => {
        generateTabsGroupedSettings[Int, List[Setting[_, _]]](weaponMap, 0, settings => settings.nonEmpty, weaponAttach, weaponNum => if(weaponNum == 0) "Any" else "Weapon slot " + weaponNum.toString, (settings, innerAttach) => {
          innerAttach(new ScrollPane(new GridBagPanel {
	    val c = new Constraints
      	    c.insets = new Insets(2, 2, 2, 2)
      	    c.fill = Fill.Horizontal
	    c.anchor = Anchor.LineStart
	    c.gridy = 0

            settings.foreach {
              case setting : Setting[_, Component] => {
                layout(setting.GUI) = c
		c.gridy = c.gridy + 1
              }
            }
	    c.weighty = 1.0
	    layout(Swing.VGlue) = c
          }))
        })
      })
    })
  }
}
