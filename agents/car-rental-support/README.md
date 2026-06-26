# car-rental-support

Demo de soporte para alquiler de coches: agente conversacional con WebSocket, RAG, reservas locales y pronóstico del tiempo vía MCP.

## Estructura

```
car-rental-support/
├── pom.xml                  # parent Maven
├── docker-compose.yml       # Postgres + Jaeger + Prometheus + Grafana
├── support-agent/           # app principal (puerto 8080)
└── weather-mcp-server/      # MCP SSE weather tools (puerto 8081)
```

## Requisitos

- Java 21
- Maven 3.9+
- Docker (para Postgres/pgvector y observabilidad)
- `OPENAI_API_KEY` exportada en el entorno

## Arranque

```bash
# 1. Infraestructura
docker compose up -d

# 2. MCP server (terminal 1)
mvn -pl weather-mcp-server spring-boot:run

# 3. Support agent (terminal 2)
mvn -pl support-agent spring-boot:run
```

O compilar todo el reactor:

```bash
mvn clean verify
```

## URLs

| Servicio | URL |
|----------|-----|
| Chat UI | http://localhost:8080 |
| WebSocket | `ws://localhost:8080/support-agent` |
| Weather MCP | http://localhost:8081 |
| Jaeger UI | http://localhost:16686 |
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3000 |

## Maven coordinates

| Módulo | artifactId |
|--------|------------|
| Parent | `car-rental-support` |
| Agente | `support-agent` |
| MCP | `weather-mcp-server` |

Paquetes Java: `com.dleon.carrental.agent`, `com.dleon.carrental.weather.mcp`.
