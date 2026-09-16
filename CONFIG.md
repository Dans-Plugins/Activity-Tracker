# Configuration Guide

Configuration is stored in `plugins/ActivityTracker/config.yml`. Options can also be changed in-game with `/at config set <option> <value>`.

## version

**Type:** string  
**Default:** *(current plugin version)*  
**Description:** The plugin version. This value is managed automatically and should not be changed manually.

## debugMode

**Type:** boolean  
**Default:** `false`  
**Description:** Enables debug logging to the server console. Useful for troubleshooting issues.

**Example:**

```yaml
debugMode: false
```

## restApiEnabled

**Type:** boolean  
**Default:** `false`  
**Description:** Enables the built-in REST API server for exposing activity data to external applications. See [REST_API.md](REST_API.md) for endpoint documentation.

**Example:**

```yaml
restApiEnabled: false
```

## restApiPort

**Type:** integer  
**Default:** `8080`  
**Description:** The port number the REST API server listens on when enabled. Change this if port 8080 is already in use on your server.

**Example:**

```yaml
restApiPort: 8080
```

## discordWebhookEnabled

**Type:** boolean  
**Default:** `false`  
**Description:** Enables Discord webhook notifications when players join or leave the server. Notifications are only sent when this is `true` **and** `discordWebhookUrl` is set to a non-empty value.

**Example:**

```yaml
discordWebhookEnabled: false
```

## discordWebhookUrl

**Type:** string  
**Default:** `""`  
**Description:** The Discord webhook URL that join and quit notifications are posted to. Leave empty to disable notifications regardless of `discordWebhookEnabled`. Surrounding whitespace is ignored.

**Example:**

```yaml
discordWebhookUrl: "https://discord.com/api/webhooks/123456789/abcdef"
```

## discordWebhookStaffOnly

**Type:** boolean  
**Default:** `false`  
**Description:** Restricts join and quit notifications to players holding the `at.staff` permission (default `op`). When `false`, notifications are sent for every player.

**Example:**

```yaml
discordWebhookStaffOnly: false
```

## discordWebhookJoinMessage

**Type:** string  
**Default:** `⚔️ **{player}** has joined the server!`  
**Description:** The message template posted when a player joins. The `{player}` placeholder is replaced with the player's name. Discord markdown is supported. An empty value suppresses join notifications.

**Example:**

```yaml
discordWebhookJoinMessage: "⚔️ **{player}** has joined the server!"
```

## discordWebhookQuitMessage

**Type:** string  
**Default:** `👋 **{player}** has left the server.`  
**Description:** The message template posted when a player leaves. The `{player}` placeholder is replaced with the player's name. Discord markdown is supported. An empty value suppresses quit notifications.

**Example:**

```yaml
discordWebhookQuitMessage: "👋 **{player}** has left the server."
```

## usage-reporting.enabled

**Type:** boolean  
**Default:** `true`  
**Description:** Whether the plugin reports usage events (see [Usage reporting](#usage-reporting) below). Set to `false` to turn it off.

**Example:**

```yaml
usage-reporting:
  enabled: true
```

## usage-reporting.endpoint

**Type:** string  
**Default:** `https://trace.danielstephenson.dev`  
**Description:** The trace server events are sent to.

**Example:**

```yaml
usage-reporting:
  endpoint: https://trace.danielstephenson.dev
```

## usage-reporting.key

**Type:** string  
**Default:** *(the plugin's key)*  
**Description:** Identifies this plugin to the trace server so reports are attributed to it. Not a secret: it ships in the default config and can only report as ActivityTracker. Empty means reporting is off regardless of `usage-reporting.enabled`.

**Example:**

```yaml
usage-reporting:
  key: "KyNSegoT_nZHAj3gCynhLWV1KWwOwK1Fy4qHqt3PeGc"
```

## Usage reporting

When the plugin is enabled, and each time one of its commands is used, a small event is sent to the
author's [trace](https://github.com/Stephenson-Software/trace-client-java) server so it is known which
plugins are actually in use. An event carries the plugin's name, the event name (`startup` or
`command`), and either the plugin version or the command name — nothing about players, the world, or
the server. Sending happens off the main thread, never delays a tick, and is dropped silently if the
server cannot be reached. Set `usage-reporting.enabled` to `false` to turn it off, or turn it off for every plugin on the server at once with `enabled: false` in `plugins/trace/config.yml` (created on first start) or the environment variable `TRACE_USAGE_REPORTING=off` / `DO_NOT_TRACK=1`. The plugin says on every startup whether reporting is on. Details: https://github.com/Stephenson-Software/trace#usage-reporting
