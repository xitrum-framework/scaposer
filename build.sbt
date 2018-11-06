enablePlugins(ScalaJSPlugin)

organization := "com.github.fbaierl"
name         := "scalajs-scaposer"
version      := "1.11.1-SNAPSHOT"

scalaVersion       := "2.12.6"
crossScalaVersions := Seq("2.12.6", "2.11.12")

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

// Scala 2.11+ core does not include scala.util.parsing.combinator
libraryDependencies += "org.scala-lang.modules" %%% "scala-parser-combinators" % "1.1.1"

libraryDependencies += "org.specs2" %%% "specs2-core" % "4.3.3" % "test"
