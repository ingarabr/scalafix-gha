val scalafixVersion = "SNAPSHOT"

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.6")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % scalafixVersion)

addSbtPlugin("org.scalablytyped.converter" % "sbt-converter" % "1.0.0-beta37")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.9.0")

libraryDependencies += "io.circe" %% "circe-core" % "0.14.1"
