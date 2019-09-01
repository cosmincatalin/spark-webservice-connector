// Run `sbt dependencyUpdates` if you want to see what dependencies can be updated

import java.text.SimpleDateFormat
import java.util.Date

organization := "com.cosminsanda"

name := "spark-webservice-connector"

version := "0.0.1"

description := "Plug-and-play implementation of an Apache Spark custom data source for web APIs."

scalaVersion := "2.11.12"

crossScalaVersions := Seq("2.11.12", "2.12.8")

libraryDependencies ++= {
    val sparkVersion = "2.4.3"
    val log4j2Version = "2.12.1"
    Seq(
        "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
        "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
        "org.apache.logging.log4j" % "log4j-api" % log4j2Version,
        "org.apache.logging.log4j" % "log4j-core" % log4j2Version,
        "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4j2Version,
        "org.slf4j" % "slf4j-api" % "1.7.25"
    )
}

scalacOptions ++= Seq("-feature", "-deprecation")

lazy val root = (project in file(".")).
  enablePlugins(BuildInfoPlugin).
  settings(
      buildInfoKeys := Seq[BuildInfoKey](
          name, version, scalaVersion, sbtVersion,
          BuildInfoKey.action("buildDate") {
              val date = new Date(System.currentTimeMillis)
              val df = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy")
              df.format(date)
          }
      ),
      buildInfoPackage := "com.cosminsanda"
  )

/**
 * Maven specific settings for publishing to Maven central.
 */
publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }

publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (version.value.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
    else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

val publishSnapshot:Command = Command.command("publishSnapshot") { state =>
    val extracted = Project extract state
    import extracted._
    val currentVersion = getOpt(version).get
    val newState = extracted.appendWithoutSession(Seq(version := s"$currentVersion-SNAPSHOT"), state)
    Project.extract(newState).runTask(PgpKeys.publishSigned in Compile, newState)
    state
}

commands ++= Seq(publishSnapshot)

pomExtra := <url>https://github.com/cosmincatalin/spark-webservice-connector</url>
  <licenses>
      <license>
          <name>Apache License, Version 2.0</name>
          <url>https://opensource.org/licenses/apache-2.0</url>
      </license>
  </licenses>
  <scm>
      <url>git@github.com:cosmincatalin/spark-webservice-connector.git</url>
      <connection>scm:git:git//github.com/cosmincatalin/spark-webservice-connector.git</connection>
      <developerConnection>scm:git:ssh://github.com:cosmincatalin/spark-webservice-connector.git</developerConnection>
  </scm>
  <developers>
      <developer>
          <id>cosmincatalin</id>
          <name>Cosmin Catalin Sanda</name>
          <email>cosmincatalin@gmail.com</email>
          <organization>N/A</organization>
          <organizationUrl>https://cosminsanda.com</organizationUrl>
      </developer>
  </developers>