# Activity Tracker

[![CI](https://github.com/Dans-Plugins/Activity-Tracker/workflows/CI/badge.svg)](https://github.com/Dans-Plugins/Activity-Tracker/actions/workflows/ci.yml)
[![Code Quality](https://github.com/Dans-Plugins/Activity-Tracker/workflows/Code%20Quality/badge.svg)](https://github.com/Dans-Plugins/Activity-Tracker/actions/workflows/code-quality.yml)
[![Java Version](https://img.shields.io/badge/Java-8%2B-blue.svg)](https://github.com/Dans-Plugins/Activity-Tracker/blob/main/pom.xml)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

## Description
Activity Tracker is an open source Minecraft plugin that tracks the activity of players.

## Performance & Testing

This plugin has been optimized for performance and includes comprehensive unit testing:

- **Algorithm Optimization**: Top player algorithm improved from O(n²) to O(n log n) complexity
- **Unit Tests**: 15+ comprehensive test cases covering edge cases and performance
- **CI/CD**: Automated testing across multiple Java versions (8, 11, 17)
- **Code Coverage**: JaCoCo integration for coverage reporting

## Server Software
This plugin was developed using the Spigot API. Users may run into trouble using it with other available server softwares like Paper.

## Installation
1) You can download the plugin from [this page](https://github.com/dmccoystephenson/Activity-Tracker/releases).

2) Once downloaded, place the jar in the plugins folder of your server files.

3) Restart your server.

## Usage
- [User Guide](https://github.com/dmccoystephenson/Activity-Tracker/wiki/Guide) (coming soon)
- [List of Commands](https://github.com/dmccoystephenson/Activity-Tracker/wiki/Commands) (coming soon)
- [FAQ](https://github.com/dmccoystephenson/Activity-Tracker/wiki/FAQ) (coming soon)

## Support
You can find the support discord server [here](https://discord.gg/xXtuAQ2).

### Experiencing a bug?
Please fill out a bug report [here](https://github.com/dmccoystephenson/Activity-Tracker/issues?q=is%3Aissue+is%3Aopen+label%3Abug).

## Roadmap
- [Known Bugs](https://github.com/dmccoystephenson/Activity-Tracker/issues?q=is%3Aopen+is%3Aissue+label%3Abug)
- [Planned Features](https://github.com/dmccoystephenson/Activity-Tracker/issues?q=is%3Aopen+is%3Aissue+label%3AEpic)
- [Planned Improvements](https://github.com/dmccoystephenson/Activity-Tracker/issues?q=is%3Aopen+is%3Aissue+label%3Aimprovement)

## Contributing

### Running Tests Locally

To run the unit tests locally:

```bash
# Run all tests
mvn test

# Run tests with coverage report
mvn test jacoco:report

# Run specific test class
mvn test -Dtest=TopRecordsAlgorithmTest

# View coverage report
open target/site/jacoco/index.html
```

### Development Requirements

- Java 8 or higher
- Maven 3.6+
- JUnit Jupiter for testing

- [Notes for Developers](https://github.com/dmccoystephenson/Activity-Tracker/wiki/Developer-Notes) (coming soon)

## Authors and acknowledgement
Name | Main Contributions
------------ | -------------
Daniel Stephenson | Creator

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
