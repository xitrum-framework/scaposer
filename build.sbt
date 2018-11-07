enablePlugins(ScalaJSPlugin)

organization := "com.github.fbaierl"
name         := "scalajs-scaposer"
version      := "0.1.1"

scalaVersion       := "2.12.6"
crossScalaVersions := Seq("2.12.6", "2.11.12")

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

// Scala 2.11+ core does not include scala.util.parsing.combinator
libraryDependencies += "org.scala-lang.modules" %%% "scala-parser-combinators" % "1.1.1"
libraryDependencies += "org.specs2" %%% "specs2-core" % "4.3.3" % "test"

// publishing
homepage := Some(url("https://github.com/fbaierl/scalajs-scaposer"))
licenses += ("MIT License", url("http://www.opensource.org/licenses/mit-license.php"))
scmInfo := Some(ScmInfo(
  url("https://github.com/fbaierl/scalajs-scaposer"),
  "scm:git:git@github.com/fbaierl/scalajs-scaposer.git",
  Some("scm:git:git@github.com/fbaierl/scalajs-scaposer.git")))
publishMavenStyle := true
isSnapshot := false
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
pomExtra :=
  <developers>
    <developer>
      <id>fbaierl</id>
      <name>Florian Baierl</name>
      <url>https://github.com/fbaierl</url>
    </developer>
  </developers>
pomIncludeRepository := { _ => false }