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

ThisBuild / githubActionPath := (root / baseDirectory).value
lazy val root = project
  .in(file("."))
  .settings(
    name := "scalafix-gha"
  )
  .aggregate(`scalafix-gha-checkrun`, example)

lazy val example = project.in(file("example"))

lazy val `scalafix-gha-checkrun` = project
  .in(file("scalafix-gha-checkrun"))
  .settings(
    Compile / npmDependencies ++= Seq(
      "@actions/core" -> "1.2.7",
      "@actions/github" -> "5.0.0",
      "@types/node" -> "17.0.23"
    ),
    libraryDependencies ++= Seq(
      "org.http4s" %%% "http4s-client" % "0.23.11",
      "org.http4s" %%% "http4s-ember-client" % "0.23.11",
      "org.http4s" %%% "http4s-circe" % "0.23.11",
      "io.circe" %%% "circe-core" % "0.14.1",
      "io.circe" %%% "circe-parser" % "0.14.1"
    ),
    stIgnore := List(
      "@octokit/core",
      "@octokit/plugin-paginate-rest",
      "@octokit/plugin-rest-endpoint-methods"
    ),
    stOutputPackage := "gha.st",
    scalaJSUseMainModuleInitializer := true,
    Compile / fastOptJS / webpackExtraArgs ++= Seq(
      "--mode=development",
      "--target=node"
    ),
    Compile / fullOptJS / webpackExtraArgs ++= Seq(
      "--mode=production",
      "--target=node"
    )
  )
  .enablePlugins(ScalablyTypedConverterPlugin)
