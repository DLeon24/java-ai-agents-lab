# Multimodal

Spring Boot service for **multimodal AI** with **OpenAI** via **Spring AI**: image generation, vision analysis, and voice processing (TTS / transcription).

## Objective

Expose HTTP endpoints for common multimodal workflows—generate images from text, analyze images with GPT-4o Vision, convert text to speech, and transcribe audio to text.

## Key Features

### Image (`/api/image`)

- `POST /api/image/generate-url` — generates a 1024×1024 image and returns `{ url, prompt }`
- `POST /api/image/generate-png` — generates a 1024×1024 image and returns `image/png` bytes

### Vision (`/api/vision`)

- `POST /api/vision/describe` — uploads an image and optional question; returns GPT-4o Vision analysis as JSON

### Voice (`/api/voice`)

- `POST /api/voice/tts` — text-to-speech (MP3); voices: `alloy`, `echo`, `fable`, `onyx`, `nova`, `shimmer`
- `POST /api/voice/transcribe` — speech-to-text via Whisper (`multipart` audio upload)

### Cross-cutting

- OpenAI integration via `spring-ai-starter-model-openai`
- Request validation and typed error responses (`400` / `502`)
- OpenAPI UI at `/swagger-ui/index.html` (`/swagger-ui.html` redirects there)
- Multipart uploads up to **25 MB** (vision + transcription)
- Tests: context load, controller validation, service unit tests (image, vision, voice)

## Stack

Java 21 · Spring Boot 3.5.5 · Spring AI 1.1.1 · OpenAI (`gpt-image-1`, `gpt-4o`, `tts-1`, `whisper-1`) · springdoc-openapi · Maven

## Project Structure

```text
multimodal/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/dleon/multimodal/
    │   │   ├── MultimodalApplication.java
    │   │   ├── config/
    │   │   │   └── OpenApiConfig.java
    │   │   ├── controller/
    │   │   │   ├── ImageController.java
    │   │   │   ├── VisionController.java
    │   │   │   └── VoiceController.java
    │   │   ├── dto/
    │   │   │   ├── ImageUrlResponse.java
    │   │   │   ├── TranscriptionResponse.java
    │   │   │   └── VisionResponse.java
    │   │   ├── exception/
    │   │   │   ├── ApiExceptionHandler.java
    │   │   │   └── ImageGenerationException.java
    │   │   └── service/
    │   │       ├── ImageService.java
    │   │       ├── VisionService.java
    │   │       ├── VoiceService.java
    │   │       └── impl/
    │   │           ├── OpenAiImageServiceImpl.java
    │   │           ├── OpenAiVisionServiceImpl.java
    │   │           └── OpenAiVoiceServiceImpl.java
    │   └── resources/
    │       └── application.yml
    └── test/
        └── java/com/dleon/multimodal/
            ├── MultimodalApplicationTests.java
            ├── controller/
            │   ├── ImageControllerValidationTest.java
            │   ├── VisionControllerValidationTest.java
            │   └── VoiceControllerValidationTest.java
            └── service/
                └── impl/
                    ├── OpenAiImageServiceImplTest.java
                    ├── OpenAiVisionServiceImplTest.java
                    └── OpenAiVoiceServiceImplTest.java
```

## Prerequisites

- Java 21
- Maven 3.x
- OpenAI API key exported as `OPENAI_API_KEY`

## Configuration

`src/main/resources/application.yml`:

| Setting | Value |
|---------|-------|
| `server.port` | `8080` |
| `spring.application.name` | `multimodal` |
| `spring.ai.openai.api-key` | `${OPENAI_API_KEY}` |
| `spring.ai.openai.chat.options.model` | `gpt-4o` (vision) |
| `spring.ai.openai.image.options.model` | `gpt-image-1` |
| `spring.ai.openai.audio.speech.options.model` | `tts-1` |
| `spring.ai.openai.audio.speech.options.voice` | `alloy` (default) |
| `spring.ai.openai.audio.transcription.options.model` | `whisper-1` |
| `spring.servlet.multipart.max-file-size` | `25MB` |

Export your API key before running:

```bash
export OPENAI_API_KEY="your-key"
```

## Run

```bash
cd multimodal
mvn spring-boot:run
```

## API

**Swagger UI:** http://localhost:8080/swagger-ui/index.html  
**OpenAPI JSON:** http://localhost:8080/v3/api-docs

### Image

| Method | Path | Params | Response |
|--------|------|--------|----------|
| `POST` | `/api/image/generate-url` | `prompt` (query) | JSON `{ "url": "...", "prompt": "..." }` |
| `POST` | `/api/image/generate-png` | `prompt` (query) | `image/png` bytes |

```bash
curl -X POST "http://localhost:8080/api/image/generate-url?prompt=A%20cyberpunk%20cat"
```

```bash
curl -X POST "http://localhost:8080/api/image/generate-png?prompt=A%20minimalist%20mountain%20logo" \
  --output image.png
```

### Vision

| Method | Path | Body (multipart) | Response |
|--------|------|------------------|----------|
| `POST` | `/api/vision/describe` | `image` (required), `question` (optional) | JSON `{ "description": "...", "question": "..." }` |

If `question` is omitted, the default prompt is: *"Describe this image in detail."*

```bash
curl -X POST http://localhost:8080/api/vision/describe \
  -F "image=@photo.jpg" \
  -F "question=What objects are visible in this image?"
```

### Voice

| Method | Path | Params / body | Response |
|--------|------|---------------|----------|
| `POST` | `/api/voice/tts` | `text` (required), `voice` (optional, default `alloy`) | `audio/mpeg` (MP3 attachment) |
| `POST` | `/api/voice/transcribe` | `audio` (multipart, required) | JSON `{ "transcription": "..." }` |

```bash
curl -X POST "http://localhost:8080/api/voice/tts?text=Hello%20world&voice=nova" \
  --output speech.mp3
```

```bash
curl -X POST http://localhost:8080/api/voice/transcribe \
  -F "audio=@recording.mp3"
```

## Errors

| HTTP | When |
|------|------|
| `400` | Validation failures, missing/empty uploads, invalid image content type |
| `502` | OpenAI image/vision failures (`ImageGenerationException`) |
