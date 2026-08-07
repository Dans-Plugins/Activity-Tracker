# Commands Reference

All commands use the base command `/at` (or `/activitytracker`).

## General Commands

### /at

**Description:** Displays plugin version and basic information.  
**Permission:** `at.default`  
**Usage:** `/at`

### /at help

**Description:** Shows a list of available commands with brief descriptions.  
**Permission:** `at.help`  
**Usage:** `/at help`

## Player Activity Commands

### /at info [playerName]

**Description:** Displays the activity record for yourself or a specified player, including total play time, login count, and last session details.  
**Permission:** `at.info`  
**Usage:** `/at info` or `/at info <playerName>`  
**Example:** `/at info Notch`

### /at average [playerName] [days]

**Description:** Shows the average daily play time for a player over a given number of days.  
**Permission:** `at.average`  
**Usage:** `/at average` or `/at average <playerName> <days>`  
**Example:** `/at average Notch 14`  
**Defaults:** Current player, 7 days

### /at top [number]

**Description:** Displays the most active players by total hours played, with visual bar indicators.  
**Permission:** `at.top`  
**Usage:** `/at top` or `/at top <number>`  
**Example:** `/at top 25`  
**Defaults:** 10 players  
**Limits:** The number must be between 1 and 100

### /at stats

**Description:** Displays server-wide activity statistics including unique player count and total logins.  
**Permission:** `at.stats`  
**Usage:** `/at stats`

## Admin Commands

### /at list

**Description:** Shows the 10 most recent player sessions with login times and duration.  
**Permission:** `at.list`  
**Usage:** `/at list`

### /at config show

**Description:** Displays all current plugin configuration options and their values.  
**Permission:** `at.config`  
**Usage:** `/at config show`

### /at config set \<option\> \<value\>

**Description:** Modifies a plugin configuration option.  
**Permission:** `at.config`  
**Usage:** `/at config set <option> <value>`  
**Example:** `/at config set debugMode true`
