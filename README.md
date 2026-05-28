## Java AI Agents Lab

Monorepo of **Generative AI projects in Java** using **Spring Boot** and **Quarkus**: agents, RAG, memory, MCP (server/client), tools, and LangChain-style orchestration with **GPT**, **Claude**, **Ollama**, plus image generation/analysis and voice pipelines (speech-to-text and text-to-speech).

### Current modules

- **`multimodal`**  
  See [`multimodal/README.md`](multimodal/README.md) for a Spring Boot multimodal service (image generation with Spring AI and OpenAI).

- **`mcp-filesystem-client`**  
  See [`mcp/mcp-filesystem-client/README.md`](mcp/mcp-filesystem-client/README.md) for a Spring Boot MCP filesystem client that exposes a REST chat endpoint backed by Google Gemini and MCP filesystem tools.

### Roadmap

This root `README.md` will be expanded as additional projects are added, documenting:

- **Spring Boot and Quarkus agents** (tool-using, multi-step flows)
- **RAG pipelines** with vector stores and document ingestion
- **Long-term memory** patterns for conversational agents
- **MCP servers and clients**, plus custom tools
- **LangChain-style orchestration** with GPT, Claude, and Ollama
- **Multimodal capabilities**: image generation/analysis and voice pipelines (STT/TTS)

