organization := "com.github.vital-software"

name := "json-annotation"

version := "0.4.0"

scalaVersion := "2.11.11"

// Largely dependent on support for https://github.com/scalamacros/paradise
crossScalaVersions := Seq("2.11.8", "2.11.9", "2.11.10", "2.11.11", "2.12.0", "2.12.1", "2.12.2")

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.6.8" % Test,
  "org.specs2" %% "specs2-core" % "3.9.5" % Test
)

unmanagedSourceDirectories in Compile <+= (sourceDirectory in Compile, scalaBinaryVersion){
  (sourceDir, version) => sourceDir / (if (version.startsWith("2.11")) "scala_2.11" else "scala_2.12")
}

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

sonatypeProfileName := "com.github.vital-software"

pomExtra := (
  <url>https://github.com/vital-software/json-annotation</url>
  <licenses>
    <license>
      <name>MIT</name>
      <url>http://opensource.org/licenses/MIT</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:vital-software/json-annotation.git</url>
    <connection>scm:git:git@github.com:vital-software/json-annotation.git</connection>
  </scm>
  <developers>
    <developer>
      <id>martinraison</id>
      <name>Martin Raison</name>
      <url>https://github.com/martinraison</url>
    </developer>
    <developer>
      <id>apatzer</id>
      <name>Aaron Patzer</name>
      <url>https://github.com/apatzer</url>
    </developer>
  </developers>)
