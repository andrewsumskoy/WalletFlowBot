lazy val versions = new {
  val enumeratum = "1.5.12"
  val doobie = "0.5.0-M11"
  val monix = "2.3.0"
  val circe = "0.9.0"
  val postgresql = "42.1.4"
}

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
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",

      "org.postgresql" % "postgresql" % versions.postgresql,
      "org.tpolecat" %% "doobie-core" % versions.doobie,
      "org.tpolecat" %% "doobie-postgres" % versions.doobie,
      "org.tpolecat" %% "doobie-hikari" % versions.doobie,
      "org.tpolecat" %% "doobie-scalatest" % versions.doobie,

      "io.circe" %% "circe-generic" % versions.circe,
      "io.circe" %% "circe-parser" % versions.circe,

      "io.monix" %% "monix" % versions.monix,
      "io.monix" %% "monix-cats" % versions.monix,
    )
  ).dependsOn(domain)