import AssemblyKeys._

name := "YetAnotherTF2ConfigGenerator"

version := "0.11"

scalaVersion := "2.9.2"

logLevel := Level.Warn

scalacOptions ++= Seq("-deprecation", "-optimise", "-explaintypes", "-verbose", "-unchecked")

libraryDependencies += "org.scala-lang" % "scala-swing" % "2.9.2"

libraryDependencies += "org.fusesource.scalate" % "scalate-page" % "1.5.3"

libraryDependencies += "com.thoughtworks.xstream" % "xstream" % "1.4.2"

ivyXML :=
  <dependencies>
    <dependency org="com.thoughtworks.xstream" name="xstream" rev="1.4.2">
      <exclude module="xmlpull"/>
    </dependency>
  </dependencies>

mainClass := Some("yetanothertf2configgenerator.GUIRunner")

assemblySettings
