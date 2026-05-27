# MCP Filesystem Client

Spring Boot MCP consumer that exposes a REST chat endpoint backed by **Google Gemini** and **MCP filesystem tools** (stdio).

## Objective

Accept natural-language messages over HTTP, forward them to Gemini with MCP filesystem tools attached, and restrict file access to a configured host directory.

## Key Features

- `POST /api/mcp/chat` — chat with filesystem-aware Gemini responses
- MCP client (stdio) spawns `@modelcontextprotocol/server-filesystem` via `npx`
- OpenAPI UI at `/swagger-ui.html`
- Integration test: `McpFilesystemClientApplicationTests.contextLoads()`

## Stack

Java 21 · Spring Boot 3.5.5 · Spring AI 1.1.1 · Gemini (`gemini-2.5-flash`) · Maven · Node.js/npx

## Project Structure

```
mcp-filesystem-client/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/dleon/mcpfilesystemclient/
    │   │   ├── McpFilesystemClientApplication.java
    │   │   ├── config/
    │   │   │   └── OpenApiConfig.java
    │   │   ├── controller/
    │   │   │   └── McpConsumerController.java
    │   │   ├── dto/
    │   │   │   ├── ChatRequest.java
    │   │   │   └── ChatResponse.java
    │   │   └── service/
    │   │       └── McpGatewayService.java
    │   └── resources/
    │       ├── application.yml
    │       └── mcp-servers.json
    └── test/
        └── java/com/dleon/mcpfilesystemclient/
            └── McpFilesystemClientApplicationTests.java
```

## Prerequisites

- Java 21, Maven 3.x, Node.js (`npx` on PATH)
- `GEMINI_API_KEY` exported to the environment
- Allowed directory exists (default: `/Users/diegoleon/personal-docs`)

```bash
mkdir -p /Users/diegoleon/personal-docs
```

Update `app.filesystem.allowed-dir` in `application.yml` for your machine.

## Configuration

Export your Gemini API key before starting the application:

```bash
export GEMINI_API_KEY="your-key"
```

## Run

```bash
cd mcp/mcp-filesystem-client
mvn spring-boot:run
```

Requires `npx`, the allowed directory, and a valid API key (boots full Spring + MCP context).

## API

| Method | Path | Body | Response |
|--------|------|------|----------|
| `POST` | `/api/mcp/chat` | `{ "message": "..." }` | `{ "response": "..." }` |

**Swagger UI:** http://localhost:8080/swagger-ui.html  
**OpenAPI JSON:** http://localhost:8080/v3/api-docs

```bash
curl -X POST http://localhost:8080/api/mcp/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"List the files in the allowed directory"}'
```