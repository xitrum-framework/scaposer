// Run sbt eclipse to create Eclipse project file
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.2.4")

// http://www.scala-js.org/news/2020/02/25/announcing-scalajs-1.0.0/#cross-building-for-scalajs-06x-and-1x
val scalaJSVersion = scala.util.Properties.envOrElse("SCALAJS_VERSION", "1.1.1")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % scalaJSVersion)
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.0.0")
