organization := "tv.cntt"

name         := "scaposer"

version      := "1.2"

scalaVersion := "2.10.1"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked"
)

// http://www.scala-sbt.org/release/docs/Detailed-Topics/Java-Sources
// Avoid problem when this lib is built with Java 7 but the projects that use it
// are run with Java 6
// java.lang.UnsupportedClassVersionError: xitrum/annotation/First : Unsupported major.minor version 51.0
javacOptions ++= Seq(
  "-source",
  "1.6"
)

libraryDependencies += "org.specs2" %% "specs2" % "1.14" % "test"
