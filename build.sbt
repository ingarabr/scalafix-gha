ThisBuild / scalaVersion := "2.13.8"

name := "scalafix-gha"

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
