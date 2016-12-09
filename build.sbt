organization := "tv.cntt"
name         := "scaposer"
version      := "1.11-SNAPSHOT"

scalaVersion       := "2.12.1"
crossScalaVersions := Seq("2.12.1", "2.11.8", "2.10.6")

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

// Scala 2.11+ core does not include scala.util.parsing.combinator
libraryDependencies := {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, scalaMajor)) if scalaMajor >= 11 =>
      libraryDependencies.value :+ "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4"
    case _ =>
      libraryDependencies.value
  }
}

libraryDependencies += "org.specs2" %% "specs2-core" % "3.8.6" % "test"
