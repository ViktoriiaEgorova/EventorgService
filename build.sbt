val circeVersion = "0.13.0"


import sbt.Keys.{scalaVersion, version}

lazy val `eventorg_final_project` = project
  .in(file("."))
  .settings(
    name in ThisBuild := "scala-global-2020",
    version in ThisBuild := "0.1",
    scalaVersion in ThisBuild := "2.13.3",
    libraryDependencies ++= Seq(

      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,

      "com.typesafe.akka" %% "akka-actor" % "2.6.10",
      "com.typesafe.akka" %% "akka-stream" % "2.6.10",
      "com.typesafe.akka" %% "akka-http" % "10.2.1",
      "de.heikoseeberger" %% "akka-http-circe" % "1.35.2",

      "com.softwaremill.macwire" %% "macros" % "2.3.7",
      "com.beachape" %% "enumeratum" % "1.6.1",
      "com.beachape" %% "enumeratum-circe" % "1.6.1",

      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "ch.qos.logback" % "logback-classic" % "1.2.3",

      "org.scalatest" %% "scalatest" % "3.2.0" % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.10" % Test,
      "com.typesafe.akka" %% "akka-http-testkit" % "10.2.1" % Test,
      "org.scalamock" %% "scalamock" % "4.4.0" % Test,

      "com.typesafe.slick" %% "slick" % "3.3.3",
      "org.scalatest" %% "scalatest" % "3.2.0" % Test,
      //"org.slf4j" % "slf4j-api" % "1.6.4",
      "org.slf4j" % "slf4j-nop" % "1.6.4",
      //"org.slf4j" % "slf4j-api" % "1.7.25",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3",
      "com.h2database" % "h2" % "1.4.200",

      "org.mindrot" % "jbcrypt" % "0.3m"


    )

  )
