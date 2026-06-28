# Car Rental Support

Maven multi-module lab with a **conversational support agent** (WebSocket + chat UI) backed by PostgreSQL/pgvector, local booking tools, RAG over rental terms, and a **weather MCP server** (SSE over HTTP) that exposes forecast tools via Open-Meteo.

## Objective

Demonstrate an end-to-end car-rental support assistant with Spring AI: the agent streams answers over WebSocket, retrieves policy context via RAG, manages bookings with local `@Tool` callbacks, connects to a remote MCP server for weather forecasts, and applies prompt-injection guardrails before each request.

## Key Features

### support-agent (port 8080)

- WebSocket chat at `/support-agent` with streaming OpenAI responses (`gpt-4o`)
- Static chat UI at `/` (`wc-chatbot` web component + import maps)
- PostgreSQL `car_rental` with JPA bookings/customers and seed data (`data.sql`)
- **RAG**: pgvector store + `QuestionAnswerAdvisor`; ingests `rag/rental-terms-of-use.txt` on startup
- **Local tools** (3): `cancelBooking`, `listBookingsForCustomer`, `getBookingDetails` — `@Tool` on `BookingTools`
- **MCP client** (SSE): connects to weather server at `http://localhost:8081` (`weather` alias)
- JDBC chat memory (max 20 messages per WebSocket session)
- Prompt-injection guardrail (dedicated `guardrailChatClient`, threshold `0.7`)
- Observability: Prometheus metrics, OTLP tracing to Jaeger, structured logs with trace/span IDs
- Tests: context load

### weather-mcp-server (port 8081)

- MCP server (WebFlux + SSE) via `spring-ai-starter-mcp-server-webflux`
- **Tools** (1): `getForecast` — latitude/longitude → 16-day Open-Meteo hourly forecast
- `@Tool` + `ToolCallbackProvider` (`ToolConfig`)
- External API: `https://api.open-meteo.com`
- Tests: context load

## Stack

Java 21 · Spring Boot 3.5.5 · Spring AI 1.0.5 · OpenAI · PostgreSQL/pgvector · MCP Java SDK (SSE) · OpenTelemetry · Prometheus · Jaeger · Grafana · Maven · Docker Compose

## Project Structure

```
car-rental-support/
├── pom.xml                          # Parent POM (modules + Spring AI BOM)
├── README.md
├── docker-compose.yml               # PostgreSQL/pgvector + Jaeger + Prometheus + Grafana
├── prometheus-config.yml
├── support-agent/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/dleon/carrental/agent/
│       │   │   ├── SupportAgentApplication.java
│       │   │   ├── agent/
│       │   │   │   └── SupportAgent.java
│       │   │   ├── config/
│       │   │   │   ├── ChatClientConfig.java
│       │   │   │   ├── ChatMemoryConfig.java
│       │   │   │   ├── RagConfig.java
│       │   │   │   └── TracingConfig.java
│       │   │   ├── constants/
│       │   │   │   └── Constants.java
│       │   │   ├── domain/
│       │   │   │   ├── Booking.java
│       │   │   │   └── Customer.java
│       │   │   ├── exception/
│       │   │   │   └── ProjectExceptions.java
│       │   │   ├── guardrail/
│       │   │   │   ├── PromptInjectionBlockedException.java
│       │   │   │   ├── PromptInjectionDetectionService.java
│       │   │   │   └── PromptInjectionGuard.java
│       │   │   ├── rag/
│       │   │   │   └── RagIngestion.java
│       │   │   ├── repository/
│       │   │   │   ├── BookingRepository.java
│       │   │   │   └── CustomerRepository.java
│       │   │   ├── tools/
│       │   │   │   └── BookingTools.java
│       │   │   ├── web/
│       │   │   │   └── ImportmapController.java
│       │   │   └── websocket/
│       │   │       ├── SupportAgentWebSocketHandler.java
│       │   │       └── WebSocketConfig.java
│       │   └── resources/
│       │       ├── application.yml
│       │       ├── data.sql
│       │       ├── rag/
│       │       │   └── rental-terms-of-use.txt
│       │       └── static/
│       │           ├── index.html
│       │           └── components/
│       └── test/
│           └── java/com/dleon/carrental/agent/
│               └── SupportAgentApplicationTest.java
└── weather-mcp-server/
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/com/dleon/carrental/weather/mcp/
        │   │   ├── WeatherMcpServerApplication.java
        │   │   ├── config/
        │   │   │   ├── RestClientConfig.java
        │   │   │   └── ToolConfig.java
        │   │   └── tools/
        │   │       └── WeatherTools.java
        │   └── resources/
        │       └── application.yml
        └── test/
            └── java/com/dleon/carrental/weather/mcp/
                └── WeatherMcpServerApplicationTest.java
```

## Architecture

```
┌─────────────┐     WebSocket /support-agent    ┌─────────────┐
│  Chat UI    │ ◄────────────────────────────►  │   Agent     │
│  :8080      │     streaming OpenAI tokens     │  :8080      │
└─────────────┘                                 └──────┬──────┘
                                                       │
         ┌─────────────────────────────────────────────┼───────────────────────┐
         │ OpenAI API                                  │ MCP SSE(weather tools)│
         ▼                                             ▼                       │
┌─────────────┐                                ┌─────────────┐                 │
│   OpenAI    │                                │   Weather   │──► Open-Meteo   │
│   API       │                                │  MCP :8081  │                 │
└─────────────┘                                └─────────────┘                 │
         ▲                                                                     │
         │ embeddings + JDBC                                                   │
         ▼                                                                     │
┌─────────────┐     pgvector RAG + bookings + chat memory                      │
│ PostgreSQL  │ ◄──────────────────────────────────────────────────────────────┘
│  :5436      │
└─────────────┘
```

## Prerequisites

- Java 21, Maven 3.x, Docker (PostgreSQL/pgvector and observability stack)
- `OPENAI_API_KEY` exported to the environment (support-agent)
- Weather MCP server running and reachable at `http://localhost:8081` before starting the agent

## Configuration

### Agent — OpenAI + MCP SSE + PostgreSQL

Export your OpenAI API key:

```bash
export OPENAI_API_KEY="your-key"
```

SSE connection (alias `weather` → server URL) in `support-agent/src/main/resources/application.yml`:

```yaml
spring.ai.mcp.client.sse.connections.weather.url: http://localhost:8081
```

PostgreSQL defaults match `docker-compose.yml`:

| Setting  | Value |
|----------|-------|
| URL      | `jdbc:postgresql://localhost:5436/car_rental` |
| User     | `postgres` |
| Password | `postgres` |
| Port     | `8080` (agent) |

Override with env vars if needed: `POSTGRES_HOST`, `POSTGRES_PORT`, `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`.

RAG and guardrail tuning (`application.yml`):

| Setting | Default | Description |
|---------|---------|-------------|
| `app.rag.max-results` | `3` | Top-K segments for `QuestionAnswerAdvisor` |
| `app.chat.memory.max-messages` | `20` | JDBC chat memory window per session |
| `app.guardrail.prompt-injection.threshold` | `0.7` | Block user input when injection score exceeds this |

### Weather MCP server — Open-Meteo

Key settings in `weather-mcp-server/src/main/resources/application.yml`:

| Setting | Value |
|---------|-------|
| Port | `8081` |
| MCP server name | `weather-service` |
| Open-Meteo base URL | `https://api.open-meteo.com` |

## Run

Start **infrastructure**, then the **weather MCP server**, then the **support agent**.

### 1. Infrastructure

PostgreSQL (pgvector), Jaeger, Prometheus, and Grafana:

```bash
cd agents/car-rental-support
docker compose up -d
```

### 2. Weather MCP server

```bash
cd agents/car-rental-support
mvn -pl weather-mcp-server spring-boot:run
```

### 3. Support agent

```bash
export OPENAI_API_KEY="your-key"
cd agents/car-rental-support
mvn -pl support-agent spring-boot:run
```

Requires a valid API key, PostgreSQL on `:5436`, and the MCP server listening on port **8081**.

### Optional — build entire reactor

```bash
cd agents/car-rental-support
mvn clean verify
```

## UI & WebSocket

| Endpoint | URL |
|----------|-----|
| Chat UI | http://localhost:8080 |
| WebSocket | `ws://localhost:8080/support-agent` |
| Actuator Prometheus | http://localhost:8080/actuator/prometheus |
| Jaeger UI | http://localhost:16686 |
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3000 |

Open the chat UI in a browser; messages are sent over WebSocket and streamed back token-by-token. Sample customers from seed data include **Speedy McWheels** (booking `1`) and **Zoom Thunderfoot** (booking `4`).

## MCP surface (weather-mcp-server)

| Type | Name | Description |
|------|------|-------------|
| Tool | `getForecast` | 16-day hourly forecast (temperature, rain, snow, precipitation) for latitude/longitude via Open-Meteo |

Tools use Spring AI `@Tool` and are registered through `ToolConfig` → `ToolCallbackProvider`.

## Local tools (support-agent)

| Tool | Description |
|------|-------------|
| `cancelBooking` | Cancel a booking (policy checks on dates) |
| `listBookingsForCustomer` | List bookings for a customer by first/last name |
| `getBookingDetails` | Fetch booking details by ID and customer name |
