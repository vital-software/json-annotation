# Changelog

## [Unreleased]

- Removed robust Json parsing implementation. Moving
this to the client package to have more control.

## [0.5.0] - 2018-08-09

### Breaking changes

- Any case class property with default values or Optional values now
fallback to defaults if parsing fails.

## [0.4.5] - 2018-06-18

### Changed

- Releases are now performed by CI automatically
- Cross-build Scala versions updated to the latest in each of 2.11.x/2.12.x
- Version moved to version.sbt
