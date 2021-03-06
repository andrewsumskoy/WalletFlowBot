lazy val versions = new {
  val enumeratum = "1.5.12"
  val doobie = "0.5.0-M11"
  val monix = "2.3.0"
  val circe = "0.9.0"
  val postgresql = "42.1.4"
}

parallelExecution in ThisBuild := false

lazy val commonSettings = Seq(
  version := "0.1.0",
  scalaVersion := "2.12.4",
  scalacOptions ++= Seq("-Ypartial-unification", "-Ymacro-expand:normal"),
  libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum" % versions.enumeratum,
    "com.beachape" %% "enumeratum-circe" % versions.enumeratum,
    "io.circe" %% "circe-core" % versions.circe,
    "org.scalatest" %% "scalatest" % "3.0.4" % Test
  ),
  addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
)

lazy val domain = project.in(file("modules/domain"))
  .settings(commonSettings)
  .settings(
    name := "myown-domain"
  )

lazy val migration = project.in(file("modules/migration"))
  .settings(commonSettings)
  .settings(
    name := "myown-migration",
    libraryDependencies ++= Seq(
      "org.flywaydb" % "flyway-core" % "5.0.4",
      "org.postgresql" % "postgresql" % versions.postgresql
    )
  )

lazy val core = project.in(file("modules/core"))
  .settings(commonSettings)
  .settings(
    name := "myown-core",
    libraryDependencies ++= Seq(
      "org.postgresql" % "postgresql" % versions.postgresql,
      "org.tpolecat" %% "doobie-core" % versions.doobie,
      "org.tpolecat" %% "doobie-postgres" % versions.doobie,
      "org.tpolecat" %% "doobie-scalatest" % versions.doobie,

      "io.circe" %% "circe-generic" % versions.circe,
      "io.circe" %% "circe-parser" % versions.circe,

      "io.monix" %% "monix" % versions.monix,
      "io.monix" %% "monix-cats" % versions.monix,
    )
  ).dependsOn(domain)

lazy val telegram = project.in(file("modules/telegram"))
  .settings(commonSettings)
  .settings(
    name := "myown-telegram",
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "doobie-hikari" % versions.doobie,
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
      "info.mukel" %% "telegrambot4s" % "3.0.14",
      "org.scalatra.scalate" %% "scalate-core" % "1.8.0",
      "com.github.pureconfig" %% "pureconfig" % "0.8.0"
    )
  ).dependsOn(core, migration)