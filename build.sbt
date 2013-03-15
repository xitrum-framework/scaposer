organization := "tv.cntt"

name         := "scaposer"

version      := "1.2"

scalaVersion := "2.10.1"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked"
)

libraryDependencies += "org.specs2" %% "specs2" % "1.12.1" % "test"
