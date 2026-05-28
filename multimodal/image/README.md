# Image Generation

Spring Boot service that exposes REST endpoints for **OpenAI image generation** using **Spring AI**.

## Objective

Generate images from text prompts over HTTP and return either a temporary OpenAI URL or raw PNG bytes.

## Key Features

- `POST /api/image/generate-url` - generates an image and returns `{ url, prompt }`
- `POST /api/image/generate-png` - generates an image and returns `image/png` bytes
- OpenAI integration via `spring-ai-starter-model-openai`
- OpenAPI UI enabled with Springdoc
- Smoke test: `ImageApplicationTests.contextLoads()`

## Stack

Java 21 · Spring Boot 3.5.5 · Spring AI 1.1.1 · OpenAI (`gpt-image-1`, `gpt-4o`) · Maven

## Project Structure

```text
image/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/dleon/image/
    │   │   ├── ImageApplication.java
    │   │   ├── config/
    │   │   │   └── OpenApiConfig.java
    │   │   ├── controller/
    │   │   │   └── ImageController.java
    │   │   ├── dto/
    │   │   │   └── ImageUrlResponse.java
    │   │   └── service/
    │   │       ├── ImageService.java
    │   │       └── impl/
    │   │           └── OpenAiImageService.java
    │   └── resources/
    │       └── application.yml
    └── test/
        └── java/com/dleon/image/
            └── ImageApplicationTests.java
```

## Prerequisites

- Java 21
- Maven 3.x
- OpenAI API key exported as `OPENAI_API_KEY`

## Configuration

`src/main/resources/application.yml`:

- `server.port=8080`
- `spring.ai.openai.api-key=${OPENAI_API_KEY}`
- `spring.ai.openai.chat.options.model=gpt-4o`
- `spring.ai.openai.image.options.model=gpt-image-1`

Export your API key before running:

```bash
export OPENAI_API_KEY="your-key"
```

## Run

```bash
cd multimodal/image
mvn spring-boot:run
```

## API

| Method | Path | Params | Response |
|--------|------|--------|----------|
| `POST` | `/api/image/generate-url` | `prompt` (query) | JSON `{ "url": "...", "prompt": "..." }` |
| `POST` | `/api/image/generate-png` | `prompt` (query) | `image/png` bytes |

**Swagger UI:** http://localhost:8080/swagger-ui/index.html  
**OpenAPI JSON:** http://localhost:8080/v3/api-docs

### Example Requests

```bash
curl -X POST "http://localhost:8080/api/image/generate-url?prompt=A%20cyberpunk%20cat"
```

```bash
curl -X POST "http://localhost:8080/api/image/generate-png?prompt=A%20minimalist%20mountain%20logo" \
  --output image.png
```
