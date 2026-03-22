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
