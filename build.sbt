organization := "tv.cntt"

name         := "scaposer"

version      := "1.2"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked"
)

// https://github.com/harrah/xsbt/wiki/Cross-Build
//crossScalaVersions := Seq("2.9.1", "2.9.2")
scalaVersion := "2.9.2"

libraryDependencies += "org.specs2" %% "specs2" % "1.11" % "test"
