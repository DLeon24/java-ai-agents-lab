# MCP STDIO (Board Games)

Maven multi-module lab with an **MCP server** (stdio transport) backed by a PostgreSQL board-game catalog, and an **MCP client** that exposes a REST chat endpoint using **OpenAI** with remote MCP tools.

## Objective

Demonstrate end-to-end MCP over **stdio** with Spring AI: the server publishes tools from a JDBC database; the client spawns the server as a subprocess, forwards MCP tool callbacks to the model, and answers natural-language questions about board games.

## Key Features

### mcp-server (stdio subprocess)

- MCP server (stdio) via `spring-ai-starter-mcp-server`
- Non-web app (`web-application-type: none`); stdout reserved for MCP protocol
- PostgreSQL `boardgamedb` with schema/data init (`schema.sql`, `data.sql`)
- **Tools** (3): `gameCount`, `findGamesForPlayerCount`, `findGamesForPlayingTime` — `@Tool` + `ToolCallbackProvider`
- Docker Compose for PostgreSQL (`compose.yaml`)

### mcp-client (port 8080)

- `POST /api/mcp/chat` — chat with OpenAI and MCP tools from the stdio server
- MCP client (stdio) launches the server JAR (`boardgamedb` alias)
- Auto-builds the server JAR on `mvn spring-boot:run` (`exec-maven-plugin`)
- Absolute JAR path baked in at build time (Maven resource filtering)
- MCP init timeout: `30s` (server cold start + DB connection)
- OpenAPI UI at `/swagger-ui/index.html` (`/swagger-ui.html` redirects there)
- Request validation and typed error responses (`400` / `502`)
- Tests: context load (disabled when MCP server JAR or DB is unavailable)

## Stack

Java 21 · Spring Boot 3.5.5 · Spring AI 1.1.7 · OpenAI · PostgreSQL · MCP Java SDK (stdio) · Maven · Docker Compose

## Project Structure

```
mcp-stdio/
├── pom.xml                          # Parent POM (modules + Spring AI BOM)
├── README.md
├── mcp-server/
│   ├── pom.xml
│   ├── compose.yaml                 # PostgreSQL for boardgamedb
│   └── src/
│       ├── main/
│       │   ├── java/com/dleon/mcpserver/
│       │   │   ├── McpServerApplication.java
│       │   │   ├── config/
│       │   │   │   └── McpConfig.java
│       │   │   ├── entity/
│       │   │   │   └── Game.java
│       │   │   ├── repository/
│       │   │   │   └── GameRepository.java
│       │   │   └── tools/
│       │   │       └── GameTools.java
│       │   └── resources/
│       │       ├── application.yml
│       │       ├── schema.sql
│       │       ├── data.sql
│       │       └── games.txt
│       └── test/
│           └── java/com/dleon/mcpserver/
│               └── McpServerApplicationTests.java
└── mcp-client/
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/com/dleon/mcpclient/
        │   │   ├── McpClientApplication.java
        │   │   ├── config/
        │   │   │   └── OpenApiConfig.java
        │   │   ├── controller/
        │   │   │   └── McpClientController.java
        │   │   ├── dto/
        │   │   │   ├── ChatRequest.java
        │   │   │   └── ChatResponse.java
        │   │   ├── exception/
        │   │   │   ├── ApiExceptionHandler.java
        │   │   │   └── McpGatewayException.java
        │   │   └── service/
        │   │       ├── McpGatewayService.java
        │   │       └── impl/
        │   │           └── McpGatewayServiceImpl.java
        │   └── resources/
        │       └── application.yml
        └── test/
            └── java/com/dleon/mcpclient/
                └── McpClientApplicationTests.java
```

## Architecture

```
┌─────────────┐     POST /api/mcp/chat      ┌─────────────┐
│   Client    │ ──────────────────────────► │   OpenAI    │
│  :8080      │ ◄────────────────────────── │   API       │
└──────┬──────┘                             └─────────────┘
       │ MCP stdio (spawns server JAR)
       ▼
┌─────────────┐     JDBC                    ┌─────────────┐
│   Server    │ ──────────────────────────► │ PostgreSQL  │
│  (stdio)    │                             │  :5432      │
└─────────────┘                             └─────────────┘
```

## Prerequisites

- Java 21, Maven 3.x, Docker (for PostgreSQL)
- `OPENAI_API_KEY` exported to the environment (client)
- PostgreSQL running and reachable from the server subprocess (`:5432`)

## Configuration

### Client — OpenAI + MCP stdio

Export your OpenAI API key:

```bash
export OPENAI_API_KEY="your-key"
```

Stdio connection (alias `boardgamedb`) in `mcp-client/src/main/resources/application.yml`:

```yaml
spring:
  ai:
    mcp:
      client:
        request-timeout: 30s
        stdio:
          connections:
            boardgamedb:
              command: java
              args:
                - -jar
                - ${MCP_SERVER_JAR:@mcp.server.jar@}   # absolute path, set at build time
        toolcallback:
          enabled: true
```

The `@mcp.server.jar@` placeholder is replaced during Maven build with the absolute path to `mcp-server/target/mcp-server-0.0.1-SNAPSHOT.jar` (see `mcp.server.jar` property in `mcp-client/pom.xml`).

Override the JAR path at runtime if needed:

| Variable | Default |
|----------|---------|
| `MCP_SERVER_JAR` | Absolute path to `mcp-server/target/mcp-server-0.0.1-SNAPSHOT.jar` (Maven-filtered) |
| `OPENAI_API_KEY` | *(required)* OpenAI API key |

### Server — STDIO + PostgreSQL

Key settings in `mcp-server/src/main/resources/application.yml`:

```yaml
spring:
  main:
    web-application-type: none   # no embedded HTTP server
    banner-mode: off
  ai:
    mcp:
      server:
        stdio: true
logging:
  threshold:
    console: OFF                 # stdout is reserved for MCP
```

PostgreSQL defaults match `compose.yaml`:

| Setting  | Value |
|----------|-------|
| URL      | `jdbc:postgresql://localhost:5432/boardgamedb` |
| User     | `myuser` |
| Password | `secret` |

> **Note:** Spring AI **1.1.7+** is required for STDIO servers. Earlier versions (e.g. 1.1.1) fail at startup with `NoClassDefFoundError: StandardServletEnvironment` because the MCP auto-configuration references `spring-web`, which is not on the STDIO classpath.

## Run

Start **PostgreSQL**, then the **client**. The client auto-builds the server JAR and spawns it as a stdio subprocess.

### 1. Database (required)

PostgreSQL must be running before the client starts (the server subprocess connects on boot). Without it, startup fails with `Connection to localhost:5432 refused`.

```bash
cd mcp/mcp-stdio/mcp-server
docker compose up -d
```

### 2. MCP client (builds server JAR + spawns via stdio)

```bash
export OPENAI_API_KEY="your-key"
cd mcp/mcp-stdio/mcp-client
mvn spring-boot:run
```

On each run, `exec-maven-plugin` packages `mcp-server` first, then starts the client. Requires a valid API key and PostgreSQL on `:5432`.

### Optional — build server JAR manually

```bash
cd mcp/mcp-stdio/mcp-server
mvn -q package -DskipTests
```

Useful when testing the server JAR in isolation or when running the client outside Maven (e.g. from an IDE after a manual build).

### Optional — run server standalone (debugging)

To verify the server in isolation (without the client):

```bash
cd mcp/mcp-stdio/mcp-server
docker compose up -d
mvn spring-boot:run
```

The process stays alive with no console output — expected for STDIO transport. Stop with `Ctrl+C`.

Test the MCP handshake manually:

```bash
printf '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2024-11-05","capabilities":{},"clientInfo":{"name":"test","version":"1.0"}}}\n' \
  | java -jar target/mcp-server-0.0.1-SNAPSHOT.jar
```

## Troubleshooting

| Symptom | Cause | Fix |
|---------|-------|-----|
| `Connection to localhost:5432 refused` | PostgreSQL not running | `docker compose up -d` in `mcp-server/` |
| `NoClassDefFoundError: StandardServletEnvironment` | Server JAR built with Spring AI \< 1.1.7 | Rebuild server: `mvn package` in `mcp-server/` (or run client — it rebuilds automatically) |
| `Client failed to initialize` / 20s timeout | Stale or missing server JAR | Run `mvn spring-boot:run` from `mcp-client/` (auto-rebuilds server) |
| Empty line JSON parse error on stdio | Server wrote logs to stdout | Ensure `logging.threshold.console: OFF` in server `application.yml` |

## API

| Method | Path | Body | Response |
|--------|------|------|----------|
| `POST` | `/api/mcp/chat` | `{ "message": "..." }` | `{ "response": "..." }` |

**Swagger UI:** http://localhost:8080/swagger-ui/index.html  
**OpenAPI JSON:** http://localhost:8080/v3/api-docs

```bash
curl -X POST http://localhost:8080/api/mcp/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"How many board games are in the database?"}'
```

## MCP surface (server)

| Type | Name / URI | Description |
|------|------------|-------------|
| Tool | `gameCount` | Total games in the repository |
| Tool | `findGamesForPlayerCount` | Games for a given player count |
| Tool | `findGamesForPlayingTime` | Games for a given duration (minutes) |