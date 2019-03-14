lazy val commonSettings = Seq(
    scalaVersion := "2.12.8",

    scalafmtOnCompile := true
)

lazy val core = project
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "1.5.0",
      "org.typelevel" %% "cats-effect" % "1.2.0",
      "co.fs2" %% "fs2-core" % "1.0.3",
      "co.fs2" %% "fs2-io" % "1.0.3",
    )
  )

lazy val server = project
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-generic" % "0.11.1",
      "io.circe" %% "circe-literal" % "0.11.1",
      "org.http4s" %% "http4s-dsl" % "0.20.0-M6",
      "org.http4s" %% "http4s-blaze-server" % "0.20.0-M6",
      "org.http4s" %% "http4s-circe" % "0.20.0-M6",
    )
  )
