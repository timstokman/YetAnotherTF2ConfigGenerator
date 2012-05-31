import AssemblyKeys._

name := "YetAnotherTF2ConfigGenerator"

version := "0.2.0"

scalaVersion := "2.9.2"

scalacOptions ++= Seq("-deprecation", "-optimise", "-explaintypes", "-verbose")

libraryDependencies += "org.scala-lang" % "scala-swing" % "2.9.2"

libraryDependencies += "org.fusesource.scalate" % "scalate-page" % "1.5.3"

mainClass := Some("yatf2configgenerator.Runner")

assemblySettings
