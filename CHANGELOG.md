# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [2.0.0-SNAPSHOT-8-8-2026] – 2026-08-08

### Changed
- Activity-Tracker is now developed AI-first. Day-to-day feature work, grooming, review and maintenance run through AI agents working directly against this repository, with the maintainers setting direction and approving what lands. The major version bump marks that change in how the project is built — it is not a break in behaviour, configuration or stored data, and existing installations can upgrade in place. Released as `2.0.0-SNAPSHOT-8-8-2026`: the AI-first line has not yet been verified in live operation, and the dated snapshot designation stays until it has.

### Added
- Optional number argument for `/at top` (e.g. `/at top 25`), defaulting to 10 and capped at 100
- DPC conventions alignment: CONTRIBUTING.md, USER_GUIDE.md, COMMANDS.md, CONFIG.md, CHANGELOG.md
- CI workflow (build.yml) following DPC conventions

### Fixed
- JUnit 4 test classes are now executed by Surefire (`junit-vintage-engine` added), raising the suite from 47 to 125 tests
- Test failures now fail the CI build; the `ci-test` profile no longer sets `maven.test.failure.ignore`
- `TopRecordsAlgorithmTest` complexity check now counts comparator invocations instead of measuring wall-clock time, removing a timing-related flake

## [1.3.0]

### Added
- REST API for exposing activity data via HTTP endpoints
- `/at average` command for viewing average daily activity
- `/at top` command algorithm optimized to O(n log n)
- Player activity ranking display in `/at info`
- Visual bar indicators in command output
