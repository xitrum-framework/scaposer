organization := "tv.cntt"
name         := "scaposer"
version      := "2.0.0-SNAPSHOT"

scalaVersion := "3.7.1"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

// Parser combinators needed for Scala 3
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.4.0"

libraryDependencies += "org.specs2" %% "specs2-core" % "5.5.8" % "test"