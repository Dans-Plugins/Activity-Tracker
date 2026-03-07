# Activity Tracker REST API

The Activity Tracker plugin exposes player activity data through a REST API, allowing external applications like server dashboards to access statistics, leaderboards, and player information.

## Configuration

The REST API is disabled by default. To enable it, configure the following options in your `config.yml`:

```yaml
restApiEnabled: true  # Set to true to enable the REST API
restApiPort: 8080     # Port number for the API server
```

You can also configure these settings in-game using the `/at config` command:
- `/at config restApiEnabled true`
- `/at config restApiPort 8080`

## API Endpoints

### Health Check
```
GET /api/health
```
Check if the API is running and responding.

**Response:**
```json
{
  "status": "OK",
  "service": "Activity Tracker REST API"
}
```

### Server Statistics
```
GET /api/stats
```
Get server-wide activity statistics.

**Response:**
```json
{
  "uniqueLogins": 150,
  "totalLogins": 2543
}
```

### Leaderboard
```
GET /api/leaderboard
```
Get the top 10 players by total hours played.

**Response:**
```json
[
  {
    "playerUuid": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "playerName": "Steve",
    "hoursPlayed": 123.45,
    "totalLogins": 87
  },
  ...
]
```

### List All Players
```
GET /api/players
```
Get a list of UUIDs for all tracked players.

**Response:**
```json
[
  "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  ...
]
```

### Player Details
```
GET /api/players/{uuid}
```
Get detailed activity information for a specific player.

**Parameters:**
- `uuid` (path): Player's UUID

**Response:**
```json
{
  "playerUuid": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "playerName": "Steve",
  "totalLogins": 87,
  "totalHoursPlayed": 123.45,
  "currentlyOnline": true,
  "firstLogin": "2023-01-15T14:30:00",
  "lastLogin": "2023-10-26T10:15:00",
  "lastLogout": null,
  "hoursSinceLogin": 2.5,
  "hoursSinceLogout": null
}
```

**Error Response (404):**
```json
{
  "error": "Player not found"
}
```

## OpenAPI Specification

A complete OpenAPI 3.0 specification is available in the `openapi.yaml` file in the plugin repository. You can use this specification with tools like:
- [Swagger UI](https://swagger.io/tools/swagger-ui/) - Interactive API documentation
- [Postman](https://www.postman.com/) - API testing and development
- Code generators for various programming languages

## CORS Support

The API includes CORS (Cross-Origin Resource Sharing) headers, allowing it to be accessed from web applications running on different domains.

## Security Considerations

⚠️ **Important Security Notes:**

1. The API does not include authentication or authorization by default
2. Anyone who can access the configured port can retrieve player data
3. Consider using a firewall to restrict access to trusted IP addresses only
4. If exposing the API to the internet, consider setting up a reverse proxy with authentication (e.g., nginx with basic auth)

## Example Usage

### Using cURL
```bash
# Get server statistics
curl http://localhost:8080/api/stats

# Get leaderboard
curl http://localhost:8080/api/leaderboard

# Get specific player info
curl http://localhost:8080/api/players/a1b2c3d4-e5f6-7890-abcd-ef1234567890
```

### Using JavaScript (fetch)
```javascript
// Get server statistics
fetch('http://localhost:8080/api/stats')
  .then(response => response.json())
  .then(data => console.log(data));

// Get leaderboard
fetch('http://localhost:8080/api/leaderboard')
  .then(response => response.json())
  .then(data => console.log(data));
```

### Using Python
```python
import requests

# Get server statistics
response = requests.get('http://localhost:8080/api/stats')
stats = response.json()
print(f"Unique logins: {stats['uniqueLogins']}")
print(f"Total logins: {stats['totalLogins']}")

# Get leaderboard
response = requests.get('http://localhost:8080/api/leaderboard')
leaderboard = response.json()
for i, player in enumerate(leaderboard, 1):
    print(f"{i}. {player['playerName']} - {player['hoursPlayed']:.2f} hours")
```

## Troubleshooting

### Port Already in Use
If you get an error that the port is already in use, either:
1. Change the `restApiPort` configuration to a different port
2. Stop the other service using that port

### Can't Connect to API
1. Verify that `restApiEnabled` is set to `true` in the config
2. Check the server logs for any error messages
3. Ensure your firewall allows connections to the configured port
4. Try accessing `http://localhost:PORT/api/health` from the server itself

### Empty Data
If endpoints return empty arrays or zero values:
1. Ensure players have logged into the server at least once
2. Check that the plugin is properly tracking player activity
3. Use the `/at stats` command in-game to verify data is being collected
