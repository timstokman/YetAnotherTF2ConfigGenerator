package yetanothertf2configgenerator.settings

import scala.swing._
import event._

abstract class DependencySetting[ValueType, GUIType <: Component, GatheredType <: Traversable[_], DependingType](createSetting : DependingType => Setting[ValueType, GUIType], val prepend : String, val combinations : Seq[DependingType], val mainCombination : DependingType, val getKey : Setting[_, _] => DependingType)(implicit gatheredManifest : Manifest[GatheredType]) extends Setting[GatheredType, GUIType] {
  val dependingSettings = combinations.map(c => createSetting(c))
  val main = createSetting(mainCombination) 
  val expandedSettings = if(showMain) main +: dependingSettings else dependingSettings
  val templateVariableDeclaration = "<%@ val " + name + ": " + gatheredManifest.toString + " %>"
  val showMain : Boolean
  val canSubscribe = false
  def validate = main.validate && dependingSettings.forall(_.validate)
  def label = main.label 
  def errorLabel = main.errorLabel
  def GUI = main.GUI
  def name = prepend + main.name.capitalize
  def settingType = main.settingType
  def weapon = main.weapon
  def valueClass = gatheredManifest.getClass
  def tf2Class = main.tf2Class
  def canSubscribeTo(s : Setting[_, _]) = false
  def validateAndError = main.validateAndError && dependingSettings.forall(_.validateAndError)

  private def setValues(settings : Seq[Setting[ValueType, GUIType]], value : ValueType) {
    settings.foreach {
      case d : DependencySetting[ValueType, GUIType, _, _] => {
        d.setInnerValue(value)
      }
      case s : Setting[ValueType, GUIType] => {
        s.value = value
      }
    }
  }

  override def innerName : String = {
    main.innerName
  }
  
  def setInnerValue(value : ValueType) {
    setValues(expandedSettings, value)
  }

  def setInnerValueFiltered(value : ValueType, filter : Setting[ValueType, GUIType] => Boolean) {
    setValues(expandedSettings.filter(filter), value)
  }

  def setInnerValueNoMain(value : ValueType) {
    setValues(dependingSettings, value)
  }

  def subscribeInner(d : DependencySetting[ValueType, GUIType, _, _]) {
    dependingSettings.foreach(dep => {
      dep.subscribe(d.asInstanceOf[dep.Sub])
    })
  }

  def unsubscribeInner(d : DependencySetting[ValueType, GUIType, _, _]) {
    dependingSettings.foreach(dep => {
      dep.removeSubscription(d.asInstanceOf[dep.Sub])
    })
  }

  def mostCommonValue = {
    dependingSettings.map(_.value).groupBy(v => v).mapValues(l => l.length).maxBy(_._2)._1
  }

  def setMainToCommon {
    unwireMain
    main.value = mostCommonValue
    wireMain
  }

  def notify(pub : Setting[_, _], ev : SettingEvent[_, _]) {
    ev match {
      case SettingChanged(oldVal : ValueType, newVal : ValueType) => {
        if(main == pub) {
          setInnerValueNoMain(newVal)
          publish(ev)
        } else {
          //not generic enough to handle more then two nestings, but that'd be too confusing anyway in a gui
          dependingSettings.foreach(s => {
            s match {
              case d : DependencySetting[ValueType, GUIType, _, _] => {
                d.setInnerValueFiltered(newVal, setting => d.getKey(pub) == d.getKey(setting))
              }
            }
          })
        }
      }
      case _ => {}
    }
  }

  def wireMain {
    main.subscribe(this.asInstanceOf[main.Sub])
    main match {
      case d : DependencySetting[ValueType, GUIType, _, _] => {
        d.subscribeInner(this)
      }
      case _ => { }
    }
  }

  def unwireMain {
    main.removeSubscription(this.asInstanceOf[main.Sub])
    main match {
      case d : DependencySetting[ValueType, GUIType, _, _] => {
        d.unsubscribeInner(this)
      }
      case _ => { }
    }
  }

  wireMain
}
