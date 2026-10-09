# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

## [1.6.0] – 2026-10-07

### Added

- Minecraft 26.3 is now a supported version: it is listed in `minecraft-versions.json` and the README, so every stable release is booted on a 26.3 server before it is published and every build checks the plugin's API use against it.

### Changed

- The vendored trace client is now 0.5.0. Every usage event carries a random server ID as the `install` tag — the `server-id` line the client appends to `plugins/trace/config.yml` on first run — so servers can be counted rather than events. The ID identifies no person, account or IP address, and deleting the line produces a new one. The startup notice, the `config.yml` comment, `CONFIG.md` and the README's usage-reporting section now say the ID is sent instead of saying nothing about the server is sent. Opting out works exactly as before.
- The usage-reporting "Details" link (startup notice, `config.yml` and the docs) now points at https://danielstephenson.dev/usage-reporting, a public page; the previous link led to a private repository and returned 404 for everyone. The vendored trace client is now 0.6.1, which carries the same link in the `plugins/trace/config.yml` header it writes. Details: https://github.com/Stephenson-Software/trace-client-java/releases/tag/0.6.1.

### Fixed

- `REST_API.md` no longer shows `"lastLogout": null` and `"hoursSinceLogout": null` in the `GET /api/players/{uuid}` example. The API omits fields with no value rather than sending `null`, and the page now says so, says which fields are absent for online, offline and session-less players, and describes the timestamp format (server-local ISO-8601, no offset, possibly with fractional seconds). It also documents the `500` `{"error": "Internal server error"}` response.

## [1.5.0] – 2026-10-02

### Added

- The supported Minecraft versions are now declared in `minecraft-versions.json` (1.19.4, 1.21.11 and 26.2) and listed in the README. Every stable release candidate is booted on a server of each, and the build checks that the plugin only uses Bukkit API that exists on all of them.

### Changed

- The vendored trace client is now 0.3.0. `plugins/trace/config.yml` can now carry a `tags:` block whose entries are added to every usage event reported by the plugins on that server, with an event's own tag winning on a key clash; release test servers write `ci: "true"` there so their boots are left out of real-installation figures. A server without a `tags:` block reports exactly as before. Details: https://github.com/Stephenson-Software/trace-client-java/releases/tag/0.3.0.
- The vendored trace client is now 0.4.0, which adds the plugin version as the `version` tag on every usage event, commands included. Previously only `startup` carried it.

### Fixed

- `openapi.yaml` no longer declares `info.version: 1.2.0`, a release in which none of the documented endpoints existed. It now reads `1.3.0`, the release that introduced the REST API and the last one to change its contract, and the convention is recorded next to the field and in `REST_API.md`: the value tracks the plugin release that last changed the API's paths, methods or shapes, and is bumped together with `pom.xml` only in such a release.
- `pom.xml` no longer declares `spark-core`, `gson`, `slf4j-simple`, `junit` and `mockito-core` twice, nor the `mockito.version` property; `junit.version` and `junit4.version`, two names for the same `4.13.2`, are collapsed into `junit4.version`. Maven was already resolving each pair to its first occurrence while warning `duplicate declaration of version`, so the resolved dependency tree is unchanged — the fix removes the warnings and the risk of a future bump to the ignored second copy silently doing nothing.
- The `Simple CI` large-dataset step no longer asserts a wall-clock bound (`duration < 100` ms), which a loaded runner could exceed for reasons unrelated to the algorithm. It now bounds comparator invocations by `2·n·log2(n)`, as `TopRecordsAlgorithmTest` already does; the sorting-order and result-size assertions are unchanged.

## [1.4.0] – 2026-09-19

Released after `2.0.0-SNAPSHOT-8-8-2026`: version numbering returned to the `1.x` line with this release, so `1.4.0` is the newer of the two despite sorting lower.

### Added

- Usage reporting is now disclosed instead of quiet: the plugin prints one line on every startup saying whether reporting is on (and, if not, why), a server-wide switch `plugins/trace/config.yml` is created on first start and turns reporting off for every plugin that reports to trace when set to `enabled: false`, the environment variables `TRACE_USAGE_REPORTING=off` and `DO_NOT_TRACK=1` are honoured, and the README gains a `Usage reporting` section that says what is sent, what is not, and every way to turn it off. The vendored trace client is 0.2.0. Nothing about what is sent has changed. Details: https://github.com/Stephenson-Software/trace#usage-reporting
- The plugin now reports usage events — `startup` on enable, `command` on each of its commands — to the author's trace server so it is known which plugins are in use. Events carry the plugin name, the event name, and the plugin version or command name; nothing about players or the server. Reporting runs off the main thread, never delays a tick, drops silently when the server is unreachable, and is turned off with `usage-reporting.enabled: false` in `config.yml` (or `/at config set usage-reporting.enabled false`). The plugin now ships a bundled `config.yml` carrying the plugin's key, so reporting is active out of the box unless turned off — including on servers upgraded from a version before the `usage-reporting` block existed, whose `config.yml` is never overwritten: the plugin reads the bundled defaults for any key the file lacks, and writes them into the file on the next startup as it already does for every other option
- Optional Discord webhook notifications for player join and quit events, configured through `discordWebhookEnabled`, `discordWebhookUrl`, `discordWebhookStaffOnly`, `discordWebhookJoinMessage` and `discordWebhookQuitMessage`. Disabled by default. When `discordWebhookStaffOnly` is enabled, only players holding the new `at.staff` permission (default `op`) trigger a notification. The HTTP request is performed off the main server thread; no Bukkit API is touched asynchronously.

### Fixed

- The `/at` header no longer prints the plugin version with a doubled `v` (`vv2.0.0-SNAPSHOT-8-8-2026`). `ActivityTracker#getVersion()` already attaches the `v` prefix, and `DefaultCommand` was prepending a second one; the literal has been dropped from the header. The value stored in `config.yml` and the version-mismatch check are untouched, so no upgrade behaviour changes.
- `/at` now honours the `at.default` permission that `COMMANDS.md` documents for it. The node was declared by `DefaultCommand` but never registered in `plugin.yml` and never checked, because the no-argument path is invoked directly instead of through Ponder's `CommandService`. It is now declared with a default of `true`, so every player keeps access unless a server operator negates it.
- `TopRecordsAlgorithmTest` no longer gates on elapsed wall-clock time. The three remaining timing assertions now count comparator invocations against an `n log n` bound, the approach already used by the complexity check, so a loaded CI runner can no longer fail the suite for reasons unrelated to the algorithm.
- Config defaults are now written on every startup rather than only when the config file is absent or its `version` key is mismatched. Existing servers whose config version already matched never received newly added keys, so new options silently defaulted without appearing in `config.yml`.
- The `Dev Release` workflow now retries publishing the `dev` prerelease before giving up. The release and its tag have to be deleted and recreated for the tag to move to the new commit, and a transient API failure inside that window previously left the repository with no `dev` release at all until the workflow was re-run by hand. Each attempt now starts from a clean slate, and an exhausted retry fails loudly.

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
