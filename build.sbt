organization := "com.vitalsoftware"

name := "json-annotation"

version := "0.3"

scalaVersion := "2.11.8"

crossScalaVersions := Seq("2.10.2", "2.10.3", "2.10.4", "2.11.0", "2.11.1", "2.11.8")

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _)

libraryDependencies ++= (
  if (scalaVersion.value.startsWith("2.10")) List("org.scalamacros" %% "quasiquotes" % "2.0.1")
  else Nil
)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.6.0-M3" % Test,
  "org.specs2" %% "specs2" % "2.3.13" % Test
)

unmanagedSourceDirectories in Compile <+= (sourceDirectory in Compile, scalaBinaryVersion){
  (sourceDir, version) => sourceDir / (if (version.startsWith("2.10")) "scala_2.10" else "scala_2.11")
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
