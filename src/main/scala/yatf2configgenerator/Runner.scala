package yatf2configgenerator

import scala.swing._
import scala.swing.event._
import Swing._
import javax.swing.UIManager
import java.io.{ File, FileInputStream, FileOutputStream }
import java.awt.Dimension

object Runner extends SwingApplication {
  val render = new ConfigRender
  render.readSettings
  var fields = collection.mutable.Map[String, Component]()

  override def startup(args: Array[String]) {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
    val top = topFrame
    top.pack
    top.visible = true
  }

  /*
   * Generate the UI
   * 
   * Really really messy code, but UI code is always messy unless you use mvc or related
   */
  def topFrame = new MainFrame {
    title = "Yet Another TF2 Config Generator"
    preferredSize = new Dimension(900, 600)
    
    contents = new TabbedPane {
      import TabbedPane._
      pages += new Page("Options", new ScrollPane {
        contents = new GridBagPanel { grid =>
          import GridBagPanel._

          val c = new Constraints
          c.fill = Fill.Horizontal
          c.weightx = 1.0
          c.weighty = 1.0
          c.insets = new Insets(3, 3, 3, 3)

          var rowNum = 0

          /*
           * Generate the option panel from the metadata
           */
          render.optionMetadata.foreach {
            case (optionName, ('options, 'int, label)) => {
              c.gridy = rowNum; c.gridx = 0
              layout(new Label(label + ":")) = c
              c.gridx = 1
              val field = new TextField(render.options(optionName).asInstanceOf[Int].toString)
              fields(optionName) = field
              layout(field) = c
              rowNum += 1
            }
            case (optionName, ('options, 'intAsBoolean, label)) => {
              c.gridy = rowNum; c.gridx = 1
              val field = new CheckBox(label) { selected = render.options(optionName).asInstanceOf[Int] == 1 }
              fields(optionName) = field
              layout(field) = c
              rowNum += 1
            }
            case (optionName, ('options, 'boolean, label)) => {
              c.gridy = rowNum; c.gridx = 1
              val field = new CheckBox(label) { selected = render.options(optionName).asInstanceOf[Boolean] }
              fields(optionName) = field
              layout(field) = c
              rowNum += 1
            }
            case (optionName, ('options, 'double, label)) => {
              c.gridy = rowNum; c.gridx = 0
              layout(new Label(label + ":")) = c
              c.gridx = 1
              val field = new TextField(render.options(optionName).asInstanceOf[Double].toString)
              fields(optionName) = field
              layout(field) = c
              rowNum += 1
            }
            case (optionName, ('options, 'network, label)) => {
              c.gridy = rowNum; c.gridx = 0
              layout(new Label(label + ":")) = c
              c.gridx = 1
              val field = new ComboBox(render.networkConfigs) { selection.item = render.options(optionName).asInstanceOf[String] }
              fields(optionName) = field
              layout(field) = c
              rowNum += 1
            }
            case (optionName, ('options, 'graphics, label)) => {
              c.gridy = rowNum; c.gridx = 0
              layout(new Label(label + ":")) = c
              c.gridx = 1
              val field = new ComboBox(render.graphicsConfigs) { selection.item = render.options(optionName).asInstanceOf[String] }
              fields(optionName) = field
              layout(field) = c
              rowNum += 1
            }
            case (optionName, ('options, 'classList, label)) => {
              c.gridy = rowNum; c.gridx = 0
              layout(new Label(label + ":")) = c
              c.gridx = 1
              val list = new ListView(render.options(optionName).asInstanceOf[List[String]]) { border = EtchedBorder(Raised) }
              fields(optionName) = list
              layout(list) = c
              c.gridy = rowNum + 1; c.gridx = 1
              val classNamesCombo = new ComboBox(render.disguiseClassNumberMap.keys.toSeq)
              layout(classNamesCombo) = c
              c.gridx = 0
              layout(new Button("Add") {
                reactions += {
                  case ButtonClicked(_) => {
                    list.listData = list.listData :+ classNamesCombo.selection.item
                  }
                }
              }) = c
              c.gridy = rowNum + 2; c.gridx = 0
              layout(new Button("Remove") {
                reactions += {
                  case ButtonClicked(_) => {
                    if (list.selection.items.size > 0)
                      list.listData = list.listData.filterNot(el => list.selection.items.exists(_ == el))
                  }
                }
              }) = c

              rowNum += 3
            }
            case (optionName, ('options, 'classDisguiseList, label)) => {
              c.gridy = rowNum; c.gridx = 0
              layout(new Label(label + ":")) = c
              c.gridx = 1
              val list = new ListView(render.options(optionName).asInstanceOf[List[Int]]) {
                renderer = ListView.Renderer(item => render.disguiseNumberClassMap(item))
                border = EtchedBorder(Raised)
              }
              fields(optionName) = list
              layout(list) = c
              c.gridy = rowNum + 1; c.gridx = 1
              val classNamesCombo = new ComboBox(render.disguiseClassNumberMap.keys.toSeq)
              layout(classNamesCombo) = c
              c.gridx = 0
              layout(new Button("Add") {
                reactions += {
                  case ButtonClicked(_) => {
                    list.listData :+= render.disguiseClassNumberMap(classNamesCombo.selection.item)
                  }
                }
              }) = c
              c.gridy = rowNum + 2; c.gridx = 0
              layout(new Button("Remove") {
                reactions += {
                  case ButtonClicked(_) => {
                    if (list.peer.getSelectedValues.size > 0)
                      list.listData = list.listData.filterNot(el => list.selection.items.exists(_ == el))
                  }
                }
              }) = c

              rowNum += 3
            }
            case _ => {}
          }
        }
      })

      pages += new Page("Weapons", new ScrollPane {
        contents = new GridBagPanel { grid =>
          import GridBagPanel._

          val c = new Constraints
          c.fill = Fill.Horizontal
          c.weightx = 1.0
          c.weighty = 1.0
          c.insets = new Insets(3, 3, 3, 3)

          var rowNum = 0
          val fieldSize = new Dimension(50, 22)

          /*
           * Generate the weapon panel
           */
          for (slot <- 1 to 3) {
            c.gridy = rowNum; c.gridx = 0
            layout(new Label("Weapon " + slot.toString + ":")) = c

            val weaponColors = render.options("weaponColors").asInstanceOf[List[(Int, Int, Int)]]
            val (red, green, blue) = (new TextField(weaponColors(slot - 1)._1.toString), 
                                      new TextField(weaponColors(slot - 1)._2.toString) ,
                                      new TextField(weaponColors(slot - 1)._3.toString))
            fields("weaponColorsRed" + slot) = red
            fields("weaponColorsGreen" + slot) = green
            fields("weaponColorsBlue" + slot) = blue
            c.gridy = rowNum + 1; c.gridx = 0
            layout(new Label("Crosshair color - Red:")) = c
            c.gridx = 1
            layout(red) = c
            c.gridy = rowNum + 2; c.gridx = 0
            layout(new Label("Crosshair color - Green:")) = c
            c.gridx = 1
            layout(green) = c
            c.gridy = rowNum + 3; c.gridx = 0
            layout(new Label("Crosshair color - Blue:")) = c
            c.gridx = 1
            layout(blue) = c

            val weaponScale = new TextField(render.options("weaponScales").asInstanceOf[List[Int]](slot - 1).toString) 
            fields("weaponScales" + slot) = weaponScale
            c.gridy = rowNum + 4; c.gridx = 0
            layout(new Label("Crosshair scale:")) = c
            c.gridx = 1
            layout(weaponScale) = c

            val weaponCrosshair = new ComboBox(render.crosshairTypes) { selection.item = render.options("weaponCrosshairs").asInstanceOf[List[String]](slot - 1) }
            fields("weaponCrosshairs" + slot) = weaponCrosshair
            c.gridy = rowNum + 5; c.gridx = 0
            layout(new Label("Crosshair:")) = c
            c.gridx = 1
            layout(weaponCrosshair) = c

            val weaponShow = new CheckBox("Show weapon") { selected = render.options("weaponShow").asInstanceOf[List[Boolean]](slot - 1) }
            fields("weaponShow" + slot) = weaponShow
            c.gridy = rowNum + 6; c.gridx = 1
            layout(weaponShow) = c

            rowNum += 7
          }

          c.gridy = rowNum; c.gridx = 0
          layout(new Label("Class specific settings:")) = c

          c.gridy = rowNum + 1; c.gridx = 0; c.gridwidth = 6
          layout(new TabbedPane {
            for (tfClass <- render.disguiseClassNumberMap.keys) {
              pages += new Page(tfClass, new GridBagPanel { grid =>
                preferredSize = new Dimension(850, 1100)
                val c = new Constraints
                c.fill = Fill.Horizontal
                c.weightx = 1.0
                c.weighty = 1.0
                c.insets = new Insets(3, 3, 3, 3)
                c.gridy = 0
                c.gridx = 1

                val classEnabled = new CheckBox("Settings " + tfClass) {
                  selected = render.options("classSpecificEnabled").asInstanceOf[Map[String, Boolean]](tfClass)
                  
                  reactions += {
                    case ButtonClicked(_) => {
                      toggleClassSpecificProfile(selected, tfClass)
                    }
                  }
                }

                fields("classSpecificEnabled" + tfClass) = classEnabled
                layout(classEnabled) = c

                var rowNum = 1

                for (slot <- 1 to 3) {
                  c.gridy = rowNum; c.gridx = 0
                  layout(new Label("Weapon " + slot.toString + ":")) = c

                  val classWeaponColors = render.options("classWeaponColors").asInstanceOf[Map[String, List[(Int, Int, Int)]]]
                  val weaponColors = render.options("weaponColors").asInstanceOf[List[(Int, Int, Int)]]
                  val (red, green, blue) = (new TextField(classWeaponColors.get(tfClass).map(_(slot - 1)._1).getOrElse { weaponColors(slot - 1)._1 }.toString), 
                                            new TextField(classWeaponColors.get(tfClass).map(_(slot - 1)._2).getOrElse { weaponColors(slot - 1)._2 }.toString), 
                                            new TextField(classWeaponColors.get(tfClass).map(_(slot - 1)._3).getOrElse { weaponColors(slot - 1)._3 }.toString))
                  fields("classWeaponColorsRed" + tfClass + slot) = red
                  fields("classWeaponColorsGreen" + tfClass + slot) = green
                  fields("classWeaponColorsBlue" + tfClass + slot) = blue
                  c.gridy = rowNum + 1; c.gridx = 0
                  layout(new Label("Crosshair color - Red:")) = c
                  c.gridx = 1
                  layout(red) = c
                  c.gridy = rowNum + 2; c.gridx = 0
                  layout(new Label("Crosshair color - Green:")) = c
                  c.gridx = 1
                  layout(green) = c
                  c.gridy = rowNum + 3; c.gridx = 0
                  layout(new Label("Crosshair color - Blue:")) = c
                  c.gridx = 1
                  layout(blue) = c

                  val weaponScale = new TextField(render.options("classWeaponScales").asInstanceOf[Map[String, List[Int]]].get(tfClass).map(_(slot - 1)).getOrElse { render.options("weaponScales").asInstanceOf[List[Int]](slot - 1) }.toString)
                  fields("classWeaponScales" + tfClass + slot) = weaponScale
                  c.gridy = rowNum + 4; c.gridx = 0
                  layout(new Label("Crosshair scale:")) = c
                  c.gridx = 1
                  layout(weaponScale) = c

                  val weaponCrosshair = new ComboBox(render.crosshairTypes) { selection.item = render.options("classWeaponCrosshairs").asInstanceOf[Map[String, List[String]]].get(tfClass).map(_(slot - 1)).getOrElse { render.options("weaponCrosshairs").asInstanceOf[List[String]](slot - 1) }.toString }
                  fields("classWeaponCrosshairs" + tfClass + slot) = weaponCrosshair
                  c.gridy = rowNum + 5; c.gridx = 0
                  layout(new Label("Crosshair:")) = c
                  c.gridx = 1
                  layout(weaponCrosshair) = c

                  val weaponSensitivity = new TextField(render.options("classSensitivity").asInstanceOf[Map[String, List[Double]]].get(tfClass).map(_(slot - 1)).getOrElse { render.options("sensitivity").asInstanceOf[Double] }.toString)
                  fields("classSensitivity" + tfClass + slot) = weaponSensitivity
                  c.gridy = rowNum + 6; c.gridx = 0
                  layout(new Label("Sensitivity:")) = c
                  c.gridx = 1
                  layout(weaponSensitivity) = c

                  val weaponDingMax = new TextField(render.options("classDingPitchMax").asInstanceOf[Map[String, List[Int]]].get(tfClass).map(_(slot - 1)).getOrElse { render.options("dingPitchMax").asInstanceOf[Int] }.toString) 
                  val weaponDingMin = new TextField(render.options("classDingPitchMin").asInstanceOf[Map[String, List[Int]]].get(tfClass).map(_(slot - 1)).getOrElse { render.options("dingPitchMin").asInstanceOf[Int] }.toString)
                  val weaponDingVolume = new TextField(render.options("classDingVolume").asInstanceOf[Map[String, List[Double]]].get(tfClass).map(_(slot - 1)).getOrElse { render.options("dingVolume").asInstanceOf[Double] }.toString)
                  fields("classDingPitchMax" + tfClass + slot) = weaponDingMax
                  fields("classDingPitchMin" + tfClass + slot) = weaponDingMin
                  fields("classDingVolume" + tfClass + slot) = weaponDingVolume
                  c.gridy = rowNum + 7; c.gridx = 0
                  layout(new Label("Ding max pitch:")) = c
                  c.gridx = 1
                  layout(weaponDingMax) = c
                  c.gridy = rowNum + 8; c.gridx = 0
                  layout(new Label("Ding min pitch:")) = c
                  c.gridx = 1
                  layout(weaponDingMin) = c
                  c.gridy = rowNum + 9; c.gridx = 0
                  layout(new Label("Ding volume:")) = c
                  c.gridx = 1
                  layout(weaponDingVolume) = c

                  val weaponShow = new CheckBox("Show Weapon") { selected = render.options("classWeaponShow").asInstanceOf[Map[String, List[Boolean]]].get(tfClass).map(_(slot - 1)).getOrElse { render.options("weaponShow").asInstanceOf[List[Boolean]](slot - 1) } }
                  fields("classWeaponShow" + tfClass + slot) = weaponShow
                  c.gridy = rowNum + 10; c.gridx = 1
                  layout(weaponShow) = c

                  rowNum += 11
                }
                
                toggleClassSpecificProfile(classEnabled.selected, tfClass)
              })
            }
          }) = c
        }
      })

      pages += new Page("Binds", new ScrollPane {
        contents = new GridBagPanel { grid =>
          import GridBagPanel._

          val c = new Constraints
          c.fill = Fill.Horizontal
          c.insets = new Insets(3, 3, 3, 3)
          c.weightx = 1.0
          c.weighty = 1.0

          var rowNum = 0

          /*
           * Generate the binds panel from metadata
           */
          render.optionMetadata.foreach {
            case (optionName, ('binds, 'key, label)) => {
              c.gridy = rowNum; c.gridx = 0
              layout(new Label(label + ":")) = c
              c.gridx = 1
              fields(optionName) = new ComboBox(render.validKeyStrings) { selection.item = render.options(optionName).asInstanceOf[String] }
              layout(fields(optionName)) = c
              rowNum += 1
            }
            case (optionName, ('binds, 'keyList, label)) => {
              c.gridy = rowNum; c.gridx = 0
              layout(new Label(label + ":")) = c
              c.gridx = 1
              val list = new ListView(render.options("unbindList").asInstanceOf[List[String]]) { border = EtchedBorder(Raised) }
              fields(optionName) = list
              layout(list) = c

              c.gridy = rowNum + 1; c.gridx = 0
              val keyCombo = new ComboBox("all" +: render.validKeyStrings.filterNot(_ == "nothing"))
              layout(new Button("Add") {
                reactions += {
                  case ButtonClicked(_) => {
                    list.listData :+= keyCombo.selection.item
                  }
                }
              }) = c
              c.gridx = 1
              layout(keyCombo) = c

              c.gridy = rowNum + 2; c.gridx = 0
              layout(new Button("Remove") {
                reactions += {
                  case ButtonClicked(_) => {
                    if (list.peer.getSelectedValues.size > 0)
                      list.listData = list.listData.filterNot(el => list.selection.items.exists(_ == el))
                  }
                }
              }) = c

              rowNum += 3
            }
            case _ => {}
          }
        }
      })

      pages += new Page("Generate", new ScrollPane {
        contents = new GridBagPanel { grid =>
          import GridBagPanel._

          val c = new Constraints
          c.fill = Fill.Horizontal
          c.insets = new Insets(3, 3, 3, 3)
          c.weightx = 1.0
          c.weighty = 0.0

          c.gridy = 1; c.gridx = 0
          layout(new Label("Username:")) = c
          c.gridx = 1
          val usernameCombo = new ComboBox(List[String]()) {
            peer.setModel(new javax.swing.DefaultComboBoxModel)
          }
          layout(usernameCombo) = c

          c.gridy = 0; c.gridx = 0
          layout(new Label("Steam directory:")) = c
          val steamField = new TextField("")
          c.gridx = 1
          layout(steamField) = c
          c.gridx = 2
          layout(new Button("Browse") {
            reactions += {
              case ButtonClicked(_) => {
                val chooser = new FileChooser {
                  fileSelectionMode = FileChooser.SelectionMode.DirectoriesOnly
                }
                chooser.showOpenDialog(grid)
                val selected = chooser.selectedFile
                val necessaryDirectories = List("steamapps", "steam", "steam.exe")
                println(selected.listFiles().toList.map(_.getName.toLowerCase).mkString(","))
                if (selected.listFiles().toList.map(_.getName).intersect(necessaryDirectories).size == necessaryDirectories.size) {
                  steamField.text = selected.toString
                  val commonDirs = List("common", "downloading", "sourcemods", "temp")
                  val userNames = selected.listFiles.filter(_.getName == "steamapps")(0).listFiles.filter(file => !commonDirs.exists(_ == file.getName) && file.isDirectory).map(_.getName)
                  usernameCombo.peer.removeAllItems
                  userNames.foreach(username => usernameCombo.peer.addItem(username))
                } else {
                  Dialog.showMessage(grid, "Invalid steam directory", "Error", Dialog.Message.Error)
                }
              }
            }
          }) = c

          c.gridy = 2; c.gridx = 1
          layout(new CheckBox("Custom Templates") {
            selected = false
            reactions += {
              case ButtonClicked(_) => {
                render.engine.allowReload = selected
              }
            }
          }) = c

          c.gridy = 3; c.gridx = 0
          layout(new Button("Save configuration locally for inspection") {
            reactions += {
              case ButtonClicked(_) => {
                val progressWindow = new Dialog() {
                  title = "Generating"
                  contents = new FlowPanel {
                    contents += new ProgressBar {
                      indeterminate = true
                    }
                  }
                }
                progressWindow.open
                new Thread {
                  override def run {
                    try {
                      saveUIToConfig
                      render.writeToDirectory(".")
                      progressWindow.close
                      Dialog.showMessage(grid, "Done generating the configuration files", "Info", Dialog.Message.Info)
                      val graphicsConfig = render.options("graphicsConfig").asInstanceOf[String]
                      val precEnabled = render.options("precEnabled").asInstanceOf[Boolean]
                      if (graphicsConfig != "none") {
                        Dialog.showMessage(grid, "You picked a graphics config, here is the necessary configuration information: \n" + render.graphicsConfigsInfo(graphicsConfig), "Info", Dialog.Message.Info)
                      }
                      if (precEnabled) {
                        Dialog.showMessage(grid, "You configured P-Rec. More information and downloads for P-Rec can be found at http://orangad.com.ua/", "Info", Dialog.Message.Info)
                      }
                    } catch {
                      case e: Exception => {
                        progressWindow.close
                        Dialog.showMessage(grid, "Exception: " + e.getStackTrace.map(_.toString).mkString("\n"), "Error", Dialog.Message.Error)
                      }
                    }
                  }
                }.start
              }
            }
          }) = c

          c.gridx = 1
          layout(new Button("Save configuration to tf2") {
            reactions += {
              case ButtonClicked(_) => {
                if (steamField.text != "" && usernameCombo.selection.item != "") {
                  val directory = List(steamField.text, "steamapps", usernameCombo.selection.item, "team fortress 2", "tf", "cfg").mkString(File.separator)
                  val progressWindow = new Dialog() {
                    title = "Generating"
                    contents = new FlowPanel {
                      contents += new ProgressBar {
                        indeterminate = true
                      }
                    }
                  }
                  progressWindow.open
                  new Thread {
                    override def run {
                      try {
                      saveUIToConfig
                      val dirFile = new File(directory)
                      if (!dirFile.exists)
                        dirFile.mkdir
                      render.writeToDirectory(directory)
                      progressWindow.close
                      Dialog.showMessage(grid, "Done generating the configuration files", "Info", Dialog.Message.Info)
                      val graphicsConfig = render.options("graphicsConfig").asInstanceOf[String]
                      val precEnabled = render.options("precEnabled").asInstanceOf[Boolean]
                      if (graphicsConfig != "none") {
                        Dialog.showMessage(grid, "You picked a graphics config, here is the necessary configuration information: \n" + render.graphicsConfigsInfo(graphicsConfig), "Info", Dialog.Message.Info)
                      }
                      if (precEnabled) {
                        Dialog.showMessage(grid, "You configured P-Rec. More information about and downloads for P-Rec can be found at http://orangad.com.ua/", "Info", Dialog.Message.Info)
                      }
                      } catch {
                        case e : Exception => {
                          progressWindow.close
                          Dialog.showMessage(grid, "Exception: " + e.getStackTrace.map(_.toString).mkString("\n"), "Error", Dialog.Message.Error)
                        }
                      }
                    }
                  }.start
                } else {
                  Dialog.showMessage(grid, "No steam directory or username specified", "Error", Dialog.Message.Error)
                }
              }
            }
          }) = c

          c.gridy = 4; c.gridx = 0
          layout(new Button("Reset tf2 configuration") {
            reactions += {
              case ButtonClicked(_) => {
                if (steamField.text != "" && usernameCombo.selection.item != "") {
                  val directory = List(steamField.text, "steamapps", usernameCombo.selection.item, "team fortress 2", "tf", "cfg").mkString(File.separator)
                  for (configFile <- render.configNames) {
                    new File(directory + File.separator + configFile + ".cfg").delete
                  }
                  new File(directory + File.separator + "user.scr").delete
                  copyFile(new File("templates" + File.separator + "default_reset.cfg"), new File(directory + File.separator + "config.cfg"))
                } else {
                  Dialog.showMessage(grid, "No steam directory or username specified", "Error", Dialog.Message.Error)
                }
              }
            }
          }) = c

          c.gridx = 1
          layout(new Button("Save current configuration settings for further editting later") {
            reactions += {
              case ButtonClicked(_) => {
                try {
                  saveUIToConfig
                  render.writeSettings
                } catch {
                  case e: Exception => Dialog.showMessage(grid, "Exception: " + e.getStackTrace.map(_.toString).mkString("\n"), "Error", Dialog.Message.Error)
                }
              }
            }
          }) = c

          c.gridx = 0; c.gridy = 5; c.weighty = 1.0
          layout(new FlowPanel) = c
        }
      })
    }
  }

  def toggleClassSpecificProfile(enabled: Boolean, tfClass: String) {
    for (slot <- 1 to 3) {
      for (color <- List("Red", "Green", "Blue")) {
        fields("classWeaponColors" + color + tfClass + slot).asInstanceOf[TextField].enabled = enabled
      }
      fields("classWeaponScales" + tfClass + slot).asInstanceOf[TextField].enabled = enabled
      fields("classWeaponCrosshairs" + tfClass + slot).asInstanceOf[ComboBox[String]].enabled = enabled
      fields("classSensitivity" + tfClass + slot).asInstanceOf[TextField].enabled = enabled
      fields("classDingPitchMax" + tfClass + slot).asInstanceOf[TextField].enabled = enabled
      fields("classDingPitchMin" + tfClass + slot).asInstanceOf[TextField].enabled = enabled
      fields("classWeaponShow" + tfClass + slot).asInstanceOf[CheckBox].enabled = enabled
      fields("classDingVolume" + tfClass + slot).asInstanceOf[TextField].enabled = enabled
    }
  }

  def copyFile(f1: File, f2: File) {
    val in = new FileInputStream(f1);
    val out = new FileOutputStream(f2);

    val buf = new Array[Byte](1024);
    var len = in.read(buf)
    while (len > 0) {
      out.write(buf, 0, len)
      len = in.read(buf)
    }
    in.close();
    out.close();
  }

  /*
   * Extract the info from the UI to the map necessary for the templates
   */
  def saveUIToConfig {
    render.optionMetadata.foreach {
      case (optionName, ('options, 'int, label)) => {
        render.options(optionName) = fields(optionName).asInstanceOf[TextField].text.toInt
      }
      case (optionName, ('options, 'intAsBoolean, label)) => {
        render.options(optionName) = if (fields(optionName).asInstanceOf[CheckBox].selected) 1 else 0
      }
      case (optionName, ('options, 'boolean, label)) => {
        render.options(optionName) = fields(optionName).asInstanceOf[CheckBox].selected
      }
      case (optionName, ('options, 'double, label)) => {
        render.options(optionName) = fields(optionName).asInstanceOf[TextField].text.toDouble
      }
      case (optionName, ('options, 'network, label)) => {
        render.options(optionName) = fields(optionName).asInstanceOf[ComboBox[String]].selection.item
      }
      case (optionName, ('options, 'graphics, label)) => {
        render.options(optionName) = fields(optionName).asInstanceOf[ComboBox[String]].selection.item
      }
      case (optionName, ('options, 'classList, label)) => {
        render.options(optionName) = fields(optionName).asInstanceOf[ListView[String]].listData.toList
      }
      case (optionName, ('options, 'classDisguiseList, label)) => {
        render.options(optionName) = fields(optionName).asInstanceOf[ListView[Int]].listData.toList
      }
      case (optionName, ('binds, 'key, label)) => {
        render.options(optionName) = fields(optionName).asInstanceOf[ComboBox[String]].selection.item
      }
      case (optionName, ('binds, 'keyList, label)) => {
        render.options(optionName) = fields(optionName).asInstanceOf[ListView[String]].listData.toList
      }
      case (optionName, ('weapons, 'colorList, label)) => {
        var list = List[(Int, Int, Int)]()
        for (slot <- 1 to 3) {
          list :+= ((fields(optionName + "Red" + slot).asInstanceOf[TextField].text.toInt,
            fields(optionName + "Green" + slot).asInstanceOf[TextField].text.toInt,
            fields(optionName + "Blue" + slot).asInstanceOf[TextField].text.toInt))
        }
        render.options(optionName) = list
      }
      case (optionName, ('weapons, 'crosshairList, label)) => {
        var list = List[String]()
        for (slot <- 1 to 3) {
          list :+= fields(optionName + slot).asInstanceOf[ComboBox[String]].selection.item
        }
        render.options(optionName) = list
      }
      case (optionName, ('weapons, 'scaleList, label)) => {
        var list = List[Int]()
        for (slot <- 1 to 3) {
          list :+= fields(optionName + slot).asInstanceOf[TextField].text.toInt
        }
        render.options(optionName) = list
      }
      case (optionName, ('weapons, 'showList, label)) => {
        var list = List[Boolean]()
        for (slot <- 1 to 3) {
          list :+= fields(optionName + slot).asInstanceOf[CheckBox].selected
        }
        render.options(optionName) = list
      }
      case (optionName, ('weapons, 'classEnabledList, label)) => {
        var enabled = collection.mutable.Map[String, Boolean]()
        for (className <- render.disguiseClassNumberMap.keys) {
          enabled(className) = fields(optionName + className).asInstanceOf[CheckBox].selected
        }
        render.options(optionName) = Map(enabled.toList: _*)
      }
      case (optionName, ('weapons, 'classColorList, label)) => {
        val weaponColors = collection.mutable.Map[String, List[(Int, Int, Int)]]()
        for (className <- render.disguiseClassNumberMap.keys) {
          if (render.options("classSpecificEnabled").asInstanceOf[Map[String, Boolean]](className)) {
            var colors = List[(Int, Int, Int)]()
            for (slot <- 1 to 3) {
              colors :+= ((fields(optionName + "Red" + className + slot).asInstanceOf[TextField].text.toInt,
                           fields(optionName + "Green" + className + slot).asInstanceOf[TextField].text.toInt,
                           fields(optionName + "Blue" + className + slot).asInstanceOf[TextField].text.toInt))
            }
            weaponColors(className) = colors
          }
        }
        render.options(optionName) = Map(weaponColors.toList: _*)
      }
      case (optionName, ('weapons, 'classCrosshairs, label)) => {
        val weaponCrosshairs = collection.mutable.Map[String, List[String]]()
        for (className <- render.disguiseClassNumberMap.keys) {
          if (render.options("classSpecificEnabled").asInstanceOf[Map[String, Boolean]](className)) {
            var crosshairs = List[String]()
            for (slot <- 1 to 3) {
              crosshairs :+= fields(optionName + className + slot).asInstanceOf[ComboBox[String]].selection.item
            }
            weaponCrosshairs(className) = crosshairs
          }
        }
        render.options(optionName) = Map(weaponCrosshairs.toList: _*)
      }
      case (optionName, ('weapons, 'classScaleList, label)) => {
        val weaponScales = collection.mutable.Map[String, List[Int]]()
        for (className <- render.disguiseClassNumberMap.keys) {
          if (render.options("classSpecificEnabled").asInstanceOf[Map[String, Boolean]](className)) {
            var scales = List[Int]()
            for (slot <- 1 to 3) {
              scales :+= fields(optionName + className + slot).asInstanceOf[TextField].text.toInt
            }
            weaponScales(className) = scales
          }
        }
        render.options(optionName) = Map(weaponScales.toList: _*)
      }
      case (optionName, ('weapons, 'classShowList, label)) => {
        val weaponShow = collection.mutable.Map[String, List[Boolean]]()
        for (className <- render.disguiseClassNumberMap.keys) {
          if (render.options("classSpecificEnabled").asInstanceOf[Map[String, Boolean]](className)) {
            var show = List[Boolean]()
            for (slot <- 1 to 3) {
              show :+= fields(optionName + className + slot).asInstanceOf[CheckBox].selected
            }
            weaponShow(className) = show
          }
        }
        render.options(optionName) = Map(weaponShow.toList: _*)
      }
      case (optionName, ('weapons, 'classSensitivity, label)) => {
        val weaponSensitivity = collection.mutable.Map[String, List[Double]]()
        for (className <- render.disguiseClassNumberMap.keys) {
          if (render.options("classSpecificEnabled").asInstanceOf[Map[String, Boolean]](className)) {
            var sensitivity = List[Double]()
            for (slot <- 1 to 3) {
              sensitivity :+= fields(optionName + className + slot).asInstanceOf[TextField].text.toDouble
            }
            weaponSensitivity(className) = sensitivity
          }
        }
        render.options(optionName) = Map(weaponSensitivity.toList: _*)
      }
      case (optionName, ('weapons, 'classDingPitch, label)) => {
        val weaponDing = collection.mutable.Map[String, List[Int]]()
        for (className <- render.disguiseClassNumberMap.keys) {
          if (render.options("classSpecificEnabled").asInstanceOf[Map[String, Boolean]](className)) {
            var dings = List[Int]()
            for (slot <- 1 to 3) {
              dings :+= fields(optionName + className + slot).asInstanceOf[TextField].text.toInt
            }
            weaponDing(className) = dings
          }
        }
        render.options(optionName) = Map(weaponDing.toList: _*)
      }
      case (optionName, ('weapons, 'classDingVolume, label)) => {
        val weaponVolume = collection.mutable.Map[String, List[Double]]()
        for (className <- render.disguiseClassNumberMap.keys) {
          if (render.options("classSpecificEnabled").asInstanceOf[Map[String, Boolean]](className)) {
            var volume = List[Double]()
            for (slot <- 1 to 3) {
              volume :+= fields(optionName + className + slot).asInstanceOf[TextField].text.toDouble
            }
            weaponVolume(className) = volume
          }
        }
        render.options(optionName) = Map(weaponVolume.toList: _*)
      }
      case _ => {}
    }
  }
}