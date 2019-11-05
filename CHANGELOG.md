# Changelog

## [Unreleased]

### Fixed

- Allow annotating final case classes

## [0.6.2] - 2019-11-05

### Added

- Added support for Scala 2.13 (@mslinn)

## [0.6.0] - 2018-08-13

### Removed

- Removed robust Json parsing implementation. Moving this to the client package
  to have more control.

## [0.5.0] - 2018-08-09

### Changed

- *Breaking*: Any case class property with default values or Optional values now
  fallback to defaults if parsing fails.

## [0.4.5] - 2018-06-18

### Changed

- Releases are now performed by CI automatically
- Cross-build Scala versions updated to the latest in each of 2.11.x/2.12.x
- Version moved to version.sbt


[Unreleased]: https://github.com/vital-software/json-annotation/compare/0.6.2...HEAD
[0.6.2]: https://github.com/vital-software/json-annotation/compare/0.6.0...0.6.2
[0.6.0]: https://github.com/vital-software/json-annotation/compare/0.5.0...0.6.0
[0.5.0]: https://github.com/vital-software/json-annotation/compare/0.4.5...0.5.0
[0.4.5]: https://github.com/vital-software/json-annotation/releases/tag/0.4.5
