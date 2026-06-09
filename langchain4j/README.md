# LangChain4j Tool Calling

Spring Boot service for **tool calling** with **LangChain4j** and a local **Ollama** chat model: the LLM decides when to invoke registered `@Tool` handlers for math, date/time, and country lookups.

## Objective

Expose a REST chat endpoint where natural-language questions are answered by an Ollama-backed assistant that can call Java tools at runtime—demonstrating LangChain4j `AiServices` and automatic tool selection without manual orchestration.

## Key Features

### REST chat (`/api/tool-calling`)

- `POST /api/tool-calling/chat` — sends a message; the LLM may invoke tools before returning `{ "response": "..." }`

### Tools (`@Tool`)

- **Calculator** — `add`, `subtract`, `multiply`, `divide`, `squareRoot`, `power`
- **Date** — `currentDate`, `currentDateTime`
- **Country API** — `lookupCountry` via [restcountries.com](https://restcountries.com) (no API key)

### Cross-cutting

- Ollama integration via `langchain4j-ollama-spring-boot-starter`
- LangChain4j `AiServices` proxy for the `Assistant` interface
- OpenAPI UI at `/swagger-ui/index.html` (`/swagger-ui.html` redirects there)
- Tests: context load (disabled when Ollama is unreachable)

## Stack

Java 21 · Spring Boot 3.5.5 · LangChain4j 0.36.2 · Ollama (`llama3.2`) · springdoc-openapi · Maven

## Project Structure

```text
langchain4j/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/dleon/langchain4j/
    │   │   ├── Langchain4jApplication.java
    │   │   ├── config/
    │   │   │   ├── LangChain4jConfig.java
    │   │   │   └── OpenApiConfig.java
    │   │   ├── controller/
    │   │   │   └── ToolCallingController.java
    │   │   ├── dto/
    │   │   │   ├── ChatRequest.java
    │   │   │   └── ChatResponse.java
    │   │   ├── service/
    │   │   │   └── Assistant.java
    │   │   └── tool/
    │   │       ├── CalculatorTools.java
    │   │       ├── CountryApiTools.java
    │   │       └── DateTools.java
    │   └── resources/
    │       └── application.yml
    └── test/
        └── java/com/dleon/langchain4j/
            └── Langchain4jApplicationTest.java
```

## Architecture

```
┌─────────────┐   POST /api/tool-calling/chat   ┌─────────────┐
│   Client    │ ──────────────────────────────► │  Assistant  │
│  :8081      │ ◄────────────────────────────── │ (AiServices)│
└─────────────┘                                 └──────┬──────┘
                                                       │
                       ┌───────────────────────────────┼───────────────────────────────┐
                       │                               │                               │
                       ▼                               ▼                               ▼
              ┌─────────────────┐            ┌─────────────────┐            ┌─────────────────┐
              │ CalculatorTools │            │    DateTools    │            │ CountryApiTools │
              │  (@Tool math)   │            │  (@Tool date)   │            │ (@Tool + HTTP)  │
              └─────────────────┘            └─────────────────┘            └────────┬────────┘
                                                                                     │
                                                                                     ▼
                                                                            restcountries.com
                       ┌───────────────────────────────────────────────────────────────┐
                       │                     Ollama (:11434)                         │
                       │                   ChatLanguageModel                         │
                       └───────────────────────────────────────────────────────────────┘
```

Flow: the user message reaches `Assistant.chat()` → LangChain4j sends available tools to Ollama → the model may request a tool call → LangChain4j runs the matching `@Tool` method → the result is sent back to the model for the final answer.

## Prerequisites

- Java 21
- Maven 3.x
- [Ollama](https://ollama.com) running locally with the chat model pulled

```bash
ollama pull llama3.2
```

Country lookups require outbound HTTPS to `restcountries.com`.

## Configuration

`src/main/resources/application.yml`:

| Setting | Value |
|---------|-------|
| `server.port` | `8081` |
| `spring.application.name` | `langchain4j` |
| `langchain4j.ollama.chat-model.base-url` | `${OLLAMA_BASE_URL:http://localhost:11434}` |
| `langchain4j.ollama.chat-model.model-name` | `${OLLAMA_CHAT_MODEL:llama3.2}` |
| `langchain4j.ollama.chat-model.temperature` | `0.7` |
| `langchain4j.ollama.chat-model.timeout` | `PT120S` |

Optional overrides:

```bash
export OLLAMA_BASE_URL="http://localhost:11434"
export OLLAMA_CHAT_MODEL="llama3.2"
```

## Run

Start Ollama, then the application:

```bash
cd langchain4j
mvn spring-boot:run
```

Requires Ollama listening on port **11434** with the configured model available.

## API

| Method | Path | Body | Response |
|--------|------|------|----------|
| `POST` | `/api/tool-calling/chat` | `{ "message": "..." }` | `{ "response": "..." }` |

**Swagger UI:** http://localhost:8081/swagger-ui/index.html  
**OpenAPI JSON:** http://localhost:8081/v3/api-docs

### Math

```bash
curl -X POST http://localhost:8081/api/tool-calling/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"What is 125 multiplied by 37?"}'
```

### Date

```bash
curl -X POST http://localhost:8081/api/tool-calling/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"What is today'\''s date?"}'
```

### Country lookup

```bash
curl -X POST http://localhost:8081/api/tool-calling/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"Tell me about Colombia"}'
```

## Tools

| Component | Methods | Description |
|-----------|---------|-------------|
| `CalculatorTools` | `add`, `subtract`, `multiply`, `divide`, `squareRoot`, `power` | Basic arithmetic |
| `DateTools` | `currentDate`, `currentDateTime` | Current date/time in English locale |
| `CountryApiTools` | `lookupCountry` | Name, capital, population, region, languages via REST |

Tools are registered in `LangChain4jConfig` through `AiServices.builder(Assistant.class).tools(...)`.
