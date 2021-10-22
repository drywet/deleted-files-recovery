name := "deleted-files-recovery"

version := "1.0"

scalaVersion := "2.11.12"


lazy val app = (project in file("app"))
  .settings(
    libraryDependencies ++= Seq(
      "commons-codec" % "commons-codec" % "1.15",
      "org.scalatest" %% "scalatest" % "3.2.9" % "test",
    ),
    assembly / mainClass := Some("recovery.Main"),
    assembly / assemblyJarName := "app.jar",
  )
