package yetanothertf2configgenerator

import java.io.{FileOutputStream, FileInputStream, OutputStreamWriter, File}
import com.thoughtworks.xstream.{XStream, io}
import io.xml.Xpp3Driver
import settings.Setting

object ProfileManager {
  val profileDir = "profiles"
  val profileExtension = ".xml"

  val xstream = new XStream(new Xpp3Driver)
  xstream.setMode(XStream.NO_REFERENCES);
  Setting.settings.foreach(setting => registerClass(setting.valueClass))
  xstream.alias("innerlist", classOf[collection.immutable.::[_]])
  
  def registerClass(cls : Class[_]) {
    xstream.processAnnotations(cls.asInstanceOf[Class[AnyRef]])
    xstream.omitField(cls, "bitmap$0") //exclude some scala helper fields
  }

  def getFilename(profile : String) = profileDir + File.separator + profile + profileExtension
  
  def writeProfile(profile : String, options : Map[String, _]) {
    xstream.toXML(options, new FileOutputStream(getFilename(profile)));
  }
  
  def readProfile(profile : String) = {
    val loaded = xstream.fromXML(new FileInputStream(getFilename(profile)))
    //backwards compat, ill remove this after a couple of versions
    loaded match {
      case value : collection.mutable.HashMap[String, _] => value.asInstanceOf[collection.mutable.HashMap[String, _]].toMap
      case value : Map[String, _] => value
    }
  }
}
