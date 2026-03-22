# Activity Tracker

[![Build](https://github.com/Dans-Plugins/Activity-Tracker/actions/workflows/build.yml/badge.svg)](https://github.com/Dans-Plugins/Activity-Tracker/actions/workflows/build.yml)
[![Java Version](https://img.shields.io/badge/Java-8%2B-blue.svg)](https://github.com/Dans-Plugins/Activity-Tracker/blob/main/pom.xml)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

## Description

Activity Tracker is a Minecraft plugin that tracks the activity of players. It records login sessions and play time, provides leaderboards, detailed per-player statistics, and an optional REST API for exposing activity data to external applications.

## Installation

### First Time Installation

1. Download the plugin from the [releases page](https://github.com/Dans-Plugins/Activity-Tracker/releases).
2. Place the JAR in the `plugins` folder of your Spigot server.
3. Restart your server.

### REST API (Optional)

Activity Tracker includes an optional REST API for external dashboards and applications. See [REST_API.md](REST_API.md) for setup and endpoint documentation.

## Usage

### Documentation

- [User Guide](USER_GUIDE.md) – Getting started and common scenarios
- [Commands Reference](COMMANDS.md) – Complete list of all commands
- [Configuration Guide](CONFIG.md) – Detailed configuration options

### Wiki & Additional Resources

- [Wiki Guide](https://github.com/Dans-Plugins/Activity-Tracker/wiki/Guide)
- [FAQ](https://github.com/Dans-Plugins/Activity-Tracker/wiki/FAQ)

## Support

You can find the support Discord server [here](https://discord.gg/xXtuAQ2).

### Experiencing a bug?

Please fill out a bug report [here](https://github.com/Dans-Plugins/Activity-Tracker/issues/new).

- [Known Bugs](https://github.com/Dans-Plugins/Activity-Tracker/issues?q=is%3Aopen+is%3Aissue+label%3Abug)

## Contributing

- [CONTRIBUTING.md](CONTRIBUTING.md)
- [Notes for Developers](https://github.com/Dans-Plugins/Activity-Tracker/wiki/Developer-Notes)

## Testing

### Unit Tests

Linux:

    mvn clean test

Windows:

    mvn clean test

If you see `BUILD SUCCESS`, the tests have passed.

## Development

This plugin is built with Maven and targets Java 8.

### Building

1. Clone the repository: `git clone https://github.com/Dans-Plugins/Activity-Tracker.git`
2. Build the plugin: `mvn clean package`
3. The built JAR will be in the `target/` directory.

### Manual Testing

1. Build the plugin JAR with `mvn clean package`.
2. Copy the JAR from `target/` into a local Spigot server's `plugins` folder.
3. Start or restart the server.

## Authors and Acknowledgement

### Developers

| Name | Main Contributions |
|------|--------------------|
| Daniel Stephenson | Creator |

## License

This project is licensed under the [GNU General Public License v3.0](LICENSE) (GPL-3.0).

You are free to use, modify, and distribute this software, provided that:

- Source code is made available under the same license when distributed.
- Changes are documented and attributed.
- No additional restrictions are applied.

See the [LICENSE](LICENSE) file for the full text of the GPL-3.0 license.

## Project Status

This project is in active development.

### bStats

You can view the bStats page for this plugin [here](https://bstats.org/plugin/bukkit/Activity%20Tracker/12983).

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for a release-by-release summary of changes.
