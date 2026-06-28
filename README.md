# Java AI Agents Lab

Monorepo of **Generative AI projects in Java** built with **Spring Boot**, **Spring AI**, and **LangChain4j**: conversational agents, RAG, memory, MCP (server/client), tool calling, guardrails, observability, and multimodal APIs with **OpenAI**, **Gemini**, and **Ollama**.

## Repository layout

```
java-ai-agents-lab/
├── README.md
├── agents/
│   └── car-rental-support/   # support-agent + weather-mcp-server
├── langchain4j/
├── multimodal/
└── mcp/
    ├── mcp-filesystem-client/
    ├── mcp-sse/              # mcp-server + mcp-client (SSE)
    └── mcp-stdio/            # mcp-server + mcp-client (stdio)
```

## Projects

| Project | Description | Documentation |
|---------|-------------|---------------|
| **car-rental-support** | Maven multi-module lab: WebSocket support **agent** (RAG, booking tools, chat memory, guardrails, observability) + weather **MCP server** (SSE, Open-Meteo) | [`agents/car-rental-support/README.md`](agents/car-rental-support/README.md) |
| **langchain4j** | Spring Boot tool-calling lab with **LangChain4j** and local **Ollama** (`@Tool` handlers for math, date, country API) | [`langchain4j/README.md`](langchain4j/README.md) |
| **multimodal** | Spring Boot multimodal service (image generation, vision, voice) with OpenAI and Spring AI | [`multimodal/README.md`](multimodal/README.md) |
| **mcp-filesystem-client** | Spring Boot MCP client (stdio): REST chat backed by **Google Gemini** and filesystem tools via `npx` | [`mcp/mcp-filesystem-client/README.md`](mcp/mcp-filesystem-client/README.md) |
| **mcp-sse** | Maven multi-module lab: MCP **server** (SSE + PostgreSQL board-game catalog) and MCP **client** (REST chat with **OpenAI**) | [`mcp/mcp-sse/README.md`](mcp/mcp-sse/README.md) |
| **mcp-stdio** | Same board-game catalog as **mcp-sse**, but MCP over **stdio** (client spawns server JAR) | [`mcp/mcp-stdio/README.md`](mcp/mcp-stdio/README.md) |
