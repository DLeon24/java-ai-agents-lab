# Java AI Agents Lab

Monorepo of **Generative AI projects in Java** using **Spring Boot** and **Quarkus**: agents, RAG, memory, MCP (server/client), tools, and LangChain-style orchestration with **GPT**, **Claude**, **Ollama**, plus image generation/analysis and voice pipelines (speech-to-text and text-to-speech).

## Repository layout

```
java-ai-agents-lab/
├── README.md
├── multimodal/
├── mcp/
│   ├── mcp-filesystem-client/
│   └── mcp-sse/              # mcp-server + mcp-client
```

## Projects

| Project | Description | Documentation |
|---------|-------------|---------------|
| **multimodal** | Spring Boot multimodal service (image generation, vision, voice) with OpenAI and Spring AI | [`multimodal/README.md`](multimodal/README.md) |
| **mcp-filesystem-client** | Spring Boot MCP client (stdio): REST chat backed by **Google Gemini** and filesystem tools via `npx` | [`mcp/mcp-filesystem-client/README.md`](mcp/mcp-filesystem-client/README.md) |
| **mcp-sse** | Maven multi-module lab: MCP **server** (SSE + PostgreSQL board-game catalog) and MCP **client** (REST chat with **OpenAI**) | [`mcp/mcp-sse/README.md`](mcp/mcp-sse/README.md) |

### multimodal

See [`multimodal/README.md`](multimodal/README.md) for setup, API endpoints, and run instructions.

### mcp-filesystem-client

See [`mcp/mcp-filesystem-client/README.md`](mcp/mcp-filesystem-client/README.md) for prerequisites (`GEMINI_API_KEY`, `npx`, allowed directory), configuration, and `POST /api/mcp/chat`.

### mcp-sse

See [`mcp/mcp-sse/README.md`](mcp/mcp-sse/README.md) for the full flow: Docker PostgreSQL → MCP server (`:3001`) → MCP client (`:8080`), tools/prompts/resources, and chat API.

## Roadmap

Additional projects and documentation may cover:

- **Spring Boot and Quarkus agents** (tool-using, multi-step flows)
- **RAG pipelines** with vector stores and document ingestion
- **Long-term memory** patterns for conversational agents
- **LangChain-style orchestration** with GPT, Claude, and Ollama
