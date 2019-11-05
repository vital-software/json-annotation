import java.nio.charset.StandardCharsets
import java.nio.file.Files

import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

import scala.io.{ Codec, Source }
import scala.sys.process.ProcessLogger

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
    <developer>
      <id>mslinn</id>
      <name>Mike Slinn</name>
      <url>https://github.com/mslinn</url>
    </developer>
  </developers>)

// PGP settings
pgpPassphrase := Some(Array())
usePgpKeyHex("1bfe664d074b29f8")

// Release settings
releaseTagName              := s"${ if (releaseUseGlobalVersion.value) (version in ThisBuild).value else version.value }" // Remove v prefix
releaseTagComment           := s"Releasing ${ (version in ThisBuild).value }\n\n[skip ci]"
releaseCommitMessage        := s"Setting version to ${ (version in ThisBuild).value }\n\n[skip ci]"

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  setReleaseVersion,
  updateReleaseFiles,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)

releasePublishArtifactsAction := PgpKeys.publishSigned.value

val updateReleaseFiles = ReleaseStep { state =>
  updateLine(
    state,
    "README.md",
    """libraryDependencies += "com.github.vital-software" %% "json-annotation" % """,
    v => s"""libraryDependencies += "com.github.vital-software" %% "json-annotation" % "$v""""
  )

  updateLine(
    state,
    "CHANGELOG.md",
    "## [Unreleased]",
    v => s"## [Unreleased]\n\n## [$v] - ${java.time.LocalDate.now}"
  )
}

def updateLine(state: State, fileName: String, marker: String, replacement: String => String): State = {
  val logger = new ProcessLogger {
    override def err(s: => String): Unit = state.log.info(s)
    override def out(s: => String): Unit = state.log.info(s)
    override def buffer[T](f: => T): T = state.log.buffer(f)
  }

  val vcs = Project.extract(state).get(releaseVcs).getOrElse {
    sys.error("VCS not set")
  }

  val (version: String, _) = state.get(ReleaseKeys.versions).getOrElse {
    sys.error(s"${ ReleaseKeys.versions.label } key not set")
  }

  val fileToModify = Project.extract(state).get(baseDirectory.in(ThisBuild)) / fileName
  val lines = Source.fromFile(fileToModify)(Codec.UTF8).getLines().toList
  val lineNumber = lines.indexWhere(_.contains(marker))

  if (lineNumber == -1) {
    throw new RuntimeException(s"Could not find marker '$marker' in file '${ fileToModify.getPath }'")
  }

  val content = lines.updated(lineNumber, replacement(version)).mkString("\n") + "\n"

  Files.write(fileToModify.toPath, content.getBytes(StandardCharsets.UTF_8))
  vcs.add(fileToModify.getAbsolutePath) !! logger

  state
}
