lazy val `scaposer` = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .settings(
    organization := "tv.cntt",
    name         := "scaposer",
    version      := "1.12.0-SNAPSHOT",

    scalaVersion       := "2.13.12",
    crossScalaVersions := Seq(scalaVersion.value, "2.12.18", "3.3.1"),

    scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked"),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),

    libraryDependencies += "org.scala-lang.modules" %%% "scala-parser-combinators" % "2.3.0",

    libraryDependencies += "org.specs2" %%% "specs2-core" % "4.20.3" % Test,
  )
