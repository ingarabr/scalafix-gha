ThisBuild / scalaVersion := "2.13.8"
ThisBuild / scalacOptions ++= Seq(
  "-Ywarn-unused",
  "-P:semanticdb:synthetics:on"
)

//scalafix
ThisBuild / scalafixScalaBinaryVersion := "2.13"
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
ThisBuild / scalafixDependencies ++= Seq(
  "com.github.vovapolu" %% "scaluzzi" % "0.1.21"
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "scalafix-gha"
  )
  .aggregate(example)

lazy val example = project.in(file("example"))
