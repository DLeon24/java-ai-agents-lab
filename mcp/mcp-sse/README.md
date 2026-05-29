# MCP SSE (Board Games)

Maven multi-module lab with an **MCP server** (SSE over HTTP) backed by a PostgreSQL board-game catalog, and an **MCP client** that exposes a REST chat endpoint using **OpenAI** with remote MCP tools.

## Objective

Demonstrate end-to-end MCP over **Server-Sent Events (SSE)** with Spring AI: the server publishes tools, prompts, and resources from a JDBC database; the client connects via SSE, forwards MCP tool callbacks to the model, and answers natural-language questions about board games.

## Key Features

### mcp-server (port 3001)

- MCP server (WebMVC + SSE) via `spring-ai-starter-mcp-server-webmvc`
- PostgreSQL `boardgamedb` with schema/data init (`schema.sql`, `data.sql`)
- **Tools** (3): `gameCount`, `findGamesForPlayerCount`, `findGamesForPlayingTime` — `@Tool` + `ToolCallbackProvider`
- **Prompts** (2): `gamesForPlayerCount`, `gamesForPlayingTime` — `@McpPrompt` (auto-registered)
- **Resources** (1): `games://game-list` — `@McpResource` (auto-registered)
- Docker Compose for PostgreSQL (`compose.yaml`)

### mcp-client (port 8080)

- `POST /api/mcp/chat` — chat with OpenAI and MCP tools from the SSE server
- MCP client (SSE) connects to `http://localhost:3001` (`boardgamedb` alias)
- OpenAPI UI at `/swagger-ui/index.html` (`/swagger-ui.html` redirects there)
- Request validation and typed error responses (`400` / `502`)
- Tests: context load (disabled when MCP server is unreachable)

## Stack

Java 21 · Spring Boot 3.5.5 · Spring AI 1.1.1 · OpenAI · PostgreSQL · MCP Java SDK (SSE) · Maven · Docker Compose

## Project Structure

```
mcp-sse/
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
│       │   │   ├── prompt/
│       │   │   │   └── PromptProvider.java
│       │   │   ├── rag/
│       │   │   │   └── ResourceProvider.java
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
       │ MCP SSE (tools / prompts / resources)
       ▼
┌─────────────┐     JDBC                    ┌─────────────┐
│   Server    │ ──────────────────────────► │ PostgreSQL  │
│  :3001      │                             │  :5432      │
└─────────────┘                             └─────────────┘
```

## Prerequisites

- Java 21, Maven 3.x, Docker (for PostgreSQL)
- `OPENAI_API_KEY` exported to the environment (client)
- MCP server running and reachable at `http://localhost:3001` before starting the client

## Configuration

### Client — OpenAI + MCP SSE

Export your OpenAI API key:

```bash
export OPENAI_API_KEY="your-key"
```

SSE connection (alias `boardgamedb` → server URL) in `mcp-client/src/main/resources/application.yml`:

```yaml
spring.ai.mcp.client.sse.connections.boardgamedb.url: http://localhost:3001
```

### Server — PostgreSQL

Defaults in `mcp-server/src/main/resources/application.yml` match `compose.yaml`:

| Setting  | Value |
|----------|-------|
| URL      | `jdbc:postgresql://localhost:5432/boardgamedb` |
| User     | `myuser` |
| Password | `secret` |
| Port     | `3001` |

## Run

Start **PostgreSQL**, then the **MCP server**, then the **client**.

### 1. Database

```bash
cd mcp/mcp-sse/mcp-server
docker compose up -d
```

### 2. MCP server

```bash
cd mcp/mcp-sse/mcp-server
mvn spring-boot:run
```

Expect log lines such as: `Registered tools: 3`, `Registered resources: 1`, `Registered prompts: 2`.

### 3. MCP client

```bash
export OPENAI_API_KEY="your-key"
cd mcp/mcp-sse/mcp-client
mvn spring-boot:run
```

Requires a valid API key and the MCP server listening on port **3001**.

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
| Prompt | `gamesForPlayerCount` | Prompt template for player-count queries |
| Prompt | `gamesForPlayingTime` | Prompt template for playing-time queries |
| Resource | `games://game-list` | Plain-text list of game titles |

Prompts and resources use `org.springaicommunity.mcp.annotation.*` (transitive via Spring AI 1.1.x). Tools use Spring AI `@Tool` and are registered through `McpConfig` → `ToolCallbackProvider`.
