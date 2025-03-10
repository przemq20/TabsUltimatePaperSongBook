ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

lazy val root = (project in file("."))
  .settings(
    name := "TabsUltimatePaperSongBook"
  )

val PekkoVersion     = "1.0.3"
val PekkoHttpVersion = "1.0.1"
libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-actor-typed" % PekkoVersion,
  "org.apache.pekko" %% "pekko-stream" % PekkoVersion,
  "org.apache.pekko" %% "pekko-http" % PekkoHttpVersion,
  "org.jsoup" % "jsoup" % "1.15.3",
  "net.ruippeixotog" %% "scala-scraper" % "3.0.0",
  "org.seleniumhq.selenium" % "selenium-java" % "4.5.0",
  "com.outr" %% "scribe" % "3.15.0",
  "org.apache.poi" % "poi" % "5.3.0",
  "org.apache.poi" % "poi-ooxml" % "5.2.3",
  "org.slf4j" % "slf4j-api" % "2.0.13",
  "org.slf4j" % "slf4j-simple" % "2.0.13",
  "org.tpolecat" %% "doobie-core" % "1.0.0-RC5",
  "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC5",
  "org.typelevel" %% "cats-core" % "2.12.0",
  "org.docx4j" % "docx4j-core" % "11.4.11",
  "org.glassfish.jaxb" % "jaxb-runtime" % "4.0.5"
)
