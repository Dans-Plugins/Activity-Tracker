# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Added
- DPC conventions alignment: CONTRIBUTING.md, USER_GUIDE.md, COMMANDS.md, CONFIG.md, CHANGELOG.md
- CI workflow (build.yml) following DPC conventions

## [1.3.0]

### Added
- REST API for exposing activity data via HTTP endpoints
- `/at average` command for viewing average daily activity
- `/at top` command algorithm optimized to O(n log n)
- Discord webhook notification support
- Player activity ranking display in `/at info`
- Visual bar indicators in command output
