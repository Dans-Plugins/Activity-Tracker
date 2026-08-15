# Command Output Formatting Style Guide

This document describes the command output formatting conventions used in this project. Follow this guide to maintain consistency across all commands, or to reproduce this style in other Bukkit/Spigot plugin projects.

## Overview

All command output uses Unicode box-drawing characters for structure, Minecraft `ChatColor` codes for color, and Unicode block characters for visual progress bars.

```
┌─ Title ─ Subtitle
│ Label: Value
│ Label: Value [██████░░░░]
└─────────────────────────
```

## Structure

### Layout

Every command output block follows this structure:

1. **Empty line** — send an empty `""` message for visual separation from prior chat
2. **Header** — top border with title using `┌─`
3. **Body lines** — content rows prefixed with `│ `
4. **Footer** — bottom border using `└─────────────────────────`

### Box-Drawing Characters

| Character | Unicode | Usage |
|-----------|---------|-------|
| `┌` | U+250C | Top-left corner (header start) |
| `│` | U+2502 | Left border (body lines) |
| `└` | U+2514 | Bottom-left corner (footer start) |
| `─` | U+2500 | Horizontal line (header/footer fill) |

## Color Scheme

All colors use Bukkit's `org.bukkit.ChatColor` enum.

### Borders and Structure

| Element | Color | ChatColor |
|---------|-------|-----------|
| Box-drawing borders (`┌│└─`) | Gold | `ChatColor.GOLD` |

### Header

| Element | Color | ChatColor |
|---------|-------|-----------|
| Plugin/player name | Yellow + Bold | `ChatColor.YELLOW` + `ChatColor.BOLD` |
| Subtitle text | Gold | `ChatColor.GOLD` |

**Important:** After using `ChatColor.BOLD`, always insert `ChatColor.RESET` before continuing with other colors to prevent bold from bleeding into subsequent text.

### Body Content

| Element | Color | ChatColor |
|---------|-------|-----------|
| Labels (e.g., "Logins:", "Status:") | Gray | `ChatColor.GRAY` |
| General data values | White | `ChatColor.WHITE` |
| Positive numeric values (hours, counts) | Green | `ChatColor.GREEN` |
| Averages, rankings, commands | Aqua | `ChatColor.AQUA` |
| Negative states (Offline, Ended) | Red | `ChatColor.RED` |
| Positive states (Online, Active) | Green | `ChatColor.GREEN` |
| Visual bars | Dark Gray | `ChatColor.DARK_GRAY` |

### Error Messages

| Element | Color | ChatColor |
|---------|-------|-----------|
| Error messages | Red | `ChatColor.RED` |

## Visual Progress Bars

Use Unicode block characters to create visual indicators for metrics:

| Character | Unicode | Usage |
|-----------|---------|-------|
| `█` | U+2588 | Filled portion |
| `░` | U+2591 | Empty portion |

### Bar Format

```
[██████░░░░]
```

- Default bar length: **10 characters**
- Enclosed in square brackets `[` and `]`
- Colored with `ChatColor.DARK_GRAY`

### Scaling

Calculate the filled portion based on the value relative to its maximum:

```java
private String createBar(double value, double max) {
    int barLength = 10;
    int filled = (int) Math.min(barLength, (value / max) * barLength);
    StringBuilder bar = new StringBuilder("[");
    for (int i = 0; i < barLength; i++) {
        bar.append(i < filled ? "\u2588" : "\u2591");
    }
    bar.append("]");
    return bar.toString();
}
```

For ranking bars (where rank 1 = best), invert the scale:

```java
private String createRankBar(int rank, int totalPlayers) {
    int barLength = 10;
    int filled = (int) Math.round(((double)(totalPlayers - rank + 1) / totalPlayers) * barLength);
    filled = Math.max(0, Math.min(barLength, filled));
    StringBuilder bar = new StringBuilder("[");
    for (int i = 0; i < barLength; i++) {
        bar.append(i < filled ? "\u2588" : "\u2591");
    }
    bar.append("]");
    return bar.toString();
}
```

## Code Patterns

### Header Pattern

```java
sender.sendMessage("");
sender.sendMessage(ChatColor.GOLD + "┌─ " + ChatColor.YELLOW + ChatColor.BOLD + "Title" +
                  ChatColor.RESET + ChatColor.GOLD + " ─ Subtitle");
```

### Label-Value Line Pattern

```java
// Simple value
sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "Label: " +
                  ChatColor.WHITE + value);

// Numeric value (positive)
sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "Hours: " +
                  ChatColor.GREEN + String.format("%.2f", hours) + "h");

// Value with visual bar
sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "Progress: " +
                  ChatColor.GREEN + String.format("%.2f", value) + "h " +
                  ChatColor.DARK_GRAY + createBar(value, maxValue));
```

### Numbered List Entry Pattern

```java
sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.AQUA + "#" + count + " " +
                  ChatColor.WHITE + itemName + " " +
                  ChatColor.GREEN + String.format("%.2f", value) + "h " +
                  ChatColor.DARK_GRAY + createBar(value, maxValue));
```

### Command List Entry Pattern

```java
sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.AQUA + "/command args " +
                  ChatColor.GRAY + "- Description text.");
```

### Status Indicator Pattern

```java
// Online/Active status
String status = isActive ? ChatColor.GREEN + "Active" : ChatColor.RED + "Ended";
```

### Footer Pattern

```java
sender.sendMessage(ChatColor.GOLD + "└─────────────────────────");
```

The footer uses 25 horizontal line characters (`─`) after the corner character.

## Command Output Examples

### Info/Detail View

```
┌─ PlayerName ─ Activity Info
│ Logins:    42
│ Play Time: 156.30h
│ Ranking:   3/15 [███████░░░]
│ Status:    Online
│ Session:   1.25h since login
│ First Login: 2025-01-15T10:30:00
└─────────────────────────
```

### Statistics View

```
┌─ Activity Tracker ─ Statistics
│ Unique Players: 42
│ Total Logins:   156
└─────────────────────────
```

### Leaderboard View

```
┌─ Activity Tracker ─ Top Players
│ #1 PlayerA 156.30h [██████████]
│ #2 PlayerB 98.50h  [██████░░░░]
│ #3 PlayerC 45.20h  [██░░░░░░░░]
└─────────────────────────
```

### Help/Command List View

```
┌─ Activity Tracker ─ Commands
│ /at help - View a list of helpful commands.
│ /at info - View your activity record.
│ /at top - View the most active players.
└─────────────────────────
```

### Config View

```
┌─ Activity Tracker ─ Config
│ version:                   1.3.0
│ debugMode:                 false
│ restApiEnabled:            false
│ restApiPort:               8080
│ discordWebhookEnabled:     false
│ discordWebhookUrl:
│ discordWebhookStaffOnly:   false
│ discordWebhookJoinMessage: ⚔️ **{player}** has joined the server!
│ discordWebhookQuitMessage: 👋 **{player}** has left the server.
└─────────────────────────
```

### Session List View

```
┌─ Activity Tracker ─ Recent Sessions (5)
│ #1 PlayerA - 2025-03-07 10:30:00 (Active)
│ #2 PlayerB - 2025-03-07 09:15:00 (Ended - 45.0 min)
└─────────────────────────
```

## Label Alignment

When multiple labels appear in a block, right-pad shorter labels with spaces to align values:

```
│ Logins:    42
│ Play Time: 156.30h
│ Ranking:   3/15
│ Status:    Online
```

For config options, use consistent padding. Every label is right-padded to the width of the longest option name plus one space, so all values start in the same column:

```
│ version:                   1.3.0
│ debugMode:                 false
│ restApiEnabled:            false
│ restApiPort:               8080
│ discordWebhookStaffOnly:   false
│ discordWebhookJoinMessage: ⚔️ **{player}** has joined the server!
```

## Adapting for Other Projects

To use this style in another Bukkit/Spigot project:

1. Copy the color scheme table above as your reference
2. Use the code patterns section as templates for building output
3. Include the `createBar()` helper method in any command that displays metrics
4. Keep the footer length consistent at 25 `─` characters
5. Always prefix output with an empty line for visual separation from chat
6. Always use `ChatColor.RESET` after `ChatColor.BOLD` to prevent bleed
