package yetanothertf2configgenerator

import java.io.{File, FileOutputStream, FileInputStream}
import scala.swing.Dialog

object Util {
  def copyFile(from: File, to: File) {
    val fromStream = new FileInputStream(from)
    val toStream = new FileOutputStream(to)
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
  
  def copyDirectory(from: File, to: File) {
    if(from.isDirectory) {
      if(!to.exists)
        to.mkdir
    
      from.list.foreach(child => copyDirectory(new File(from, child), new File(to, child)))
    } else {
      copyFile(from, to)
    }
  }
  
  def copyDirFiles(from : File, to : File, pattern : String) {
    try {
      from.listFiles.foreach(fromFile => {
        if(fromFile.getName.matches(pattern)) {
          copyFile(from, to)
        }
      })
    } catch {
      case e : Exception => handleException(e)
    }
  }
  
  def handleException(e : Exception) {
    val message = "Exception: " + e.getMessage
    val longMessage = message + "\n" + e.getStackTrace.map(_.toString).mkString("\n")
    Dialog.showMessage(null, message, "Error", Dialog.Message.Error)
    Console.err.println(longMessage)
  }
}