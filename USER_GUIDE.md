# User Guide

## Prerequisites

- A Spigot-based Minecraft server (1.13 or higher)
- Java 8 or higher

## First Steps

1. Download the Activity Tracker JAR from the [releases page](https://github.com/Dans-Plugins/Activity-Tracker/releases).
2. Place the JAR into your server's `plugins` folder.
3. Restart the server.
4. Activity Tracker will begin tracking player sessions automatically.

## Common Scenarios

### Viewing Your Own Activity

Run `/at info` in-game to see your total play time, login count, and last session details.

### Viewing Another Player's Activity

Run `/at info <playerName>` to see the activity record for a specific player.

### Checking the Leaderboard

Run `/at top` to see the top 10 most active players by total hours played.

### Viewing Server-Wide Statistics

Run `/at stats` to see overall server statistics such as total unique players and total logins.

### Checking Average Daily Activity

Run `/at average` to see your average daily play time over the last 7 days, or `/at average <playerName> <days>` for a specific player and time range.

### Managing Configuration

Operators can run `/at config show` to view current settings and `/at config set <option> <value>` to change them. See [CONFIG.md](CONFIG.md) for details on all configuration options.

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `at.help` | `true` | Access the help command |
| `at.info` | `true` | View activity info for yourself or others |
| `at.top` | `true` | View the top players leaderboard |
| `at.stats` | `true` | View server-wide statistics |
| `at.average` | `true` | View average daily activity |
| `at.config` | `op` | View and modify plugin configuration |
| `at.list` | `op` | View recent player sessions |
