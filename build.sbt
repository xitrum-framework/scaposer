// shadow sbt-scalajs' crossProject and CrossType from Scala.js 0.6.x
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

organization := "tv.cntt"
name         := "scaposer"
version      := "1.11.1-SNAPSHOT"

scalaVersion       := "2.13.0"
crossScalaVersions := Seq("2.13.0", "2.12.8", "2.11.12")

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

// Scala 2.11+ core does not include scala.util.parsing.combinator
libraryDependencies += "org.scala-lang.modules" %%% "scala-parser-combinators" % "1.1.2"

libraryDependencies += "org.specs2" %%% "specs2-core" % "4.10.2" % "test"

lazy val scaposer = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)

lazy val scaposerJS = scaposer.js
lazy val scaposerJVM = scaposer.jvm

lazy val root = (project in file("."))
  .aggregate(scaposerJS, scaposerJVM)
  .settings(skip in publish := true)
