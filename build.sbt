lazy val `scaposer` = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .settings(
    name         := "scaposer",

    scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked"),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),

    libraryDependencies += "org.scala-lang.modules" %%% "scala-parser-combinators" % "2.3.0",

    libraryDependencies += "org.specs2" %%% "specs2-core" % "4.20.3" % Test,
  )

ThisBuild / scalaVersion       := "2.13.12"
ThisBuild / crossScalaVersions := Seq(scalaVersion.value, "2.12.18", "3.3.1")

ThisBuild / organization := "io.github.olegych"
ThisBuild / organizationName := "OlegYch"
ThisBuild / organizationHomepage := Some(url("https://github.com/OlegYch"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/OlegYch/scaposer"),
    "scm:git@github.com:OlegYch/scaposer.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id    = "OlegYch",
    name  = "Aleh Aleshka",
    email = "oleglbch@gmail.com",
    url   = url("https://github.com/OlegYch")
  )
)

ThisBuild / description := "GNU Gettext .po file loader for Scala"
ThisBuild / licenses := Seq("MIT" -> url("https://opensource.org/license/mit/"))
ThisBuild / homepage := Some(url("https://github.com/OlegYch/scaposer"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishMavenStyle := true
ThisBuild / publishTo := sonatypePublishToBundle.value
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeProfileName := "OlegYch"
ThisBuild / releaseCrossBuild := true

import ReleaseTransformations._
ThisBuild / versionScheme := Some("early-semver")
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
