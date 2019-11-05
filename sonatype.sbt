import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ =>
  false
}

sonatypeProfileName := "com.github.vital-software"

pomExtra := (<url>https://github.com/vital-software/json-annotation</url>
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
releaseTagName := s"${if (releaseUseGlobalVersion.value) (ThisBuild / version).value else version.value}" // Remove v0.x prefix
releaseTagComment := s"Releasing ${(ThisBuild / version).value}\n\n[skip ci]"
releaseCommitMessage := s"Setting version to ${(ThisBuild / version).value}\n\n[skip ci]"

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  setReleaseVersion,
  updateLines,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)

releasePublishArtifactsAction := PgpKeys.publishSigned.value

val unreleasedCompare = """^\[Unreleased\]: https://github\.com/(.*)/compare/(.*)\.\.\.HEAD$""".r
updateLinesSchema := Seq(
  UpdateLine(
    file("README.md"),
    _.contains("// Latest release"),
    (v, _) => s"""libraryDependencies += "com.github.vital-software" %% "json-annotation" % "$v" // Latest release"""
  ),
  UpdateLine(
    file("CHANGELOG.md"),
    _.contains("## [Unreleased]"),
    (v, _) => s"## [Unreleased]\n\n## [$v] - ${java.time.LocalDate.now}"
  ),
  UpdateLine(
    file("CHANGELOG.md"),
    unreleasedCompare.unapplySeq(_).isDefined,
    (v, compareLine) =>
      compareLine match {
        case unreleasedCompare(project, previous) =>
          s"[Unreleased]: https://github.com/$project/compare/$v...HEAD\n[$v]: https://github.com/$project/compare/$previous...$v"
      }
  ),
)
