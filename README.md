# Java AI Agents Lab

Monorepo of **Generative AI projects in Java** using **Spring Boot** and **Quarkus**: agents, RAG, memory, MCP (server/client), tools, and LangChain-style orchestration with **GPT**, **Claude**, **Ollama**, plus image generation/analysis and voice pipelines (speech-to-text and text-to-speech).

## Repository layout

```
java-ai-agents-lab/
├── README.md
├── langchain4j/
├── multimodal/
├── mcp/
│   ├── mcp-filesystem-client/
│   ├── mcp-sse/              # mcp-server + mcp-client (SSE)
│   └── mcp-stdio/            # mcp-server + mcp-client (stdio)
```

## Projects

| Project | Description | Documentation |
|---------|-------------|---------------|
| **langchain4j** | Spring Boot tool-calling lab with **LangChain4j** and local **Ollama** (`@Tool` handlers for math, date, country API) | [`langchain4j/README.md`](langchain4j/README.md) |
| **multimodal** | Spring Boot multimodal service (image generation, vision, voice) with OpenAI and Spring AI | [`multimodal/README.md`](multimodal/README.md) |
| **mcp-filesystem-client** | Spring Boot MCP client (stdio): REST chat backed by **Google Gemini** and filesystem tools via `npx` | [`mcp/mcp-filesystem-client/README.md`](mcp/mcp-filesystem-client/README.md) |
| **mcp-sse** | Maven multi-module lab: MCP **server** (SSE + PostgreSQL board-game catalog) and MCP **client** (REST chat with **OpenAI**) | [`mcp/mcp-sse/README.md`](mcp/mcp-sse/README.md) |
| **mcp-stdio** | Same board-game catalog as **mcp-sse**, but MCP over **stdio** (client spawns server JAR) | [`mcp/mcp-stdio/README.md`](mcp/mcp-stdio/README.md) |

## Roadmap

Additional projects and documentation may cover:

- **Spring Boot and Quarkus agents** (tool-using, multi-step flows)
- **RAG pipelines** with vector stores and document ingestion
- **Long-term memory** patterns for conversational agents
- **LangChain-style orchestration** with GPT, Claude, and Ollama
