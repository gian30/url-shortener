# URL Shortener Service
A simple REST service for URL shortening built with Scala.

The service accepts a full URL and returns a short, user-friendly url like:
`http://{hostname}/{code}` where `{code}` is 8 characters.
The service automatically sets an expiration date, and DynamoDB can be configured to automatically delete expired entries (check next steps section).

## Prerequisits

- JDK 17+
- sbt (Scala Build Tool)
- DynamoDB
  (or Docker to run it easily with `docker-compose`)

## Run DynamoDB with Docker

```bash
docker-compose up -d
```
Or if using Docker Desktop on Mac:
```bash
docker compose up -d
```

## Run project locally 
```bash
sbt compile
sbt run
```

## Test endpoints
- You can find a requests.http file inside the http folder, which allows you to test endpoints locally using the [REST Client extension](https://marketplace.visualstudio.com/items?itemName=humao.rest-client "REST Client extension") in VS Code.
- Just open the file and click "Send Request" above each block to test endpoints directly from VS Code.

## Future steps
- Allow users to specify expiry duration per URL
- Enable DynamoDB's automatic deletion of expired records by:
	- In AWS Console, go to DynamoDB > Tables > short_links
	- Under Time to Live, enable TTL
	- Set the attribute name to expires_at