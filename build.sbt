organization := "tv.cntt"

name         := "scaposer"

version      := "1.2"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked"
)

// http://www.scala-sbt.org/release/docs/Detailed-Topics/Cross-Build
// scalaVersion := "2.10.0"
crossScalaVersions := Seq("2.9.2", "2.10.0")

libraryDependencies += "org.specs2" %% "specs2" % "1.12.1" % "test"
