lazy val commonSettings = Seq(
  name := "escalade",
  organization := "com.zaneli",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.12.6",
  scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlint",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused",
  "-Ywarn-unused-import",
  "-Ywarn-value-discard"
  )
)

lazy val api = (project in file("api"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      guice,
      "org.skinny-framework" %% "skinny-orm" % "2.6.0",
      "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % "3.2.3",
      "com.h2database" % "h2" % "1.4.197" % Test,
      "org.scalikejdbc" %% "scalikejdbc-test" % "3.2.3" % Test,
      specs2 % Test
    )
  ).enablePlugins(PlayScala)
