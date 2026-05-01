# Spring AI Application

A test/learning project exploring [Spring AI](https://docs.spring.io/spring-ai/reference/) features including chat, streaming, RAG, structured output, prompt templating, and chat memory — using both OpenAI and Ollama models.

## Tech Stack

- Java 21, Spring Boot 3.5, Spring AI 1.1.4
- OpenAI GPT (via API)
- Ollama (local LLM — `llama3.2:1b`)
- Qdrant (vector store, via Docker Compose)
- H2 (persistent file-based DB for chat memory)
- Tika (PDF document reader)

---

## Prerequisites

### 1. Environment Variables

| Variable | Description |
|---|---|
| `OPENAI_KEY` | Your OpenAI API key |
| `TAVILY_SEARCH_API_KEY` | Tavily API key (only needed for web search RAG) |

Set them in your shell or IDE run configuration:
```bash
export OPENAI_KEY=<your-openai-api-key>
export TAVILY_SEARCH_API_KEY=<your-tavily-api-key>
```

### 2. Ollama

Install [Ollama](https://ollama.com) and pull the required model:
```bash
ollama pull llama3.2:1b
```

Ollama must be running on `http://localhost:11434` before starting the app.

### 3. Qdrant (Vector Store)

Qdrant is managed automatically via Spring Boot Docker Compose integration. Just make sure Docker is running — the app will start the container on launch using `compose.yml`.

Alternatively, run it manually:
```bash
docker run -p 6333:6333 -p 6334:6334 qdrant/qdrant
```

---

## Running the App

```bash
./mvnw spring-boot:run
```

The app starts on port **9999**.

---

## API Endpoints

All endpoints are under `http://localhost:9999/api`.

### Chat — `GET /api/chat`
Basic chat with OpenAI or Ollama.

| Param | Required | Default | Description |
|---|---|---|---|
| `message` | yes | — | User message |
| `model` | no | `ollama` | `ollama` or `openai` |

### Streaming — `GET /api/stream`
Same as `/chat` but returns a streaming `Flux<String>` response.

| Param | Required | Default | Description |
|---|---|---|---|
| `message` | yes | — | User message |
| `model` | no | `ollama` | `ollama` or `openai` |

### Prompt Stuffing — `GET /api/stuffed-chat`
Chat with a system prompt injected from `prompts/system-prompt.st`.

| Param | Required | Default | Description |
|---|---|---|---|
| `message` | yes | — | User message |
| `model` | no | `ollama` | `ollama` or `openai` |

### Prompt Template — `GET /api/email`
Generates a customer service email reply using a prompt template.

| Param | Required | Description |
|---|---|---|
| `customerName` | yes | Customer's name |
| `customerMessage` | yes | Customer's original message |
| `model` | no | `ollama` or `openai` (default: `ollama`) |

### Chat with Memory — `GET /api/chat-memory`
Stateful chat backed by H2 DB. Conversation is scoped per `username` header.

| Param/Header | Required | Description |
|---|---|---|
| `message` (param) | yes | User message |
| `username` (header) | yes | Conversation ID |

### RAG — `GET /api/rag/chat`
Manual RAG: retrieves context from Qdrant vector store and injects it into the prompt.

| Param/Header | Required | Description |
|---|---|---|
| `message` (param) | yes | User message |
| `username` (header) | yes | Conversation ID |

### HR RAG — `GET /api/rag/hr-chat`
Same as above but uses the HR-specific system prompt and the loaded HR policy PDF.

| Param/Header | Required | Description |
|---|---|---|
| `message` (param) | yes | User message |
| `username` (header) | yes | Conversation ID |

### HR RAG (Advisor) — `GET /api/rag/hr-chat2`
Fully advisor-driven RAG — retrieval and augmentation handled automatically by `RetrievalAugmentationAdvisor`.

| Param/Header | Required | Description |
|---|---|---|
| `message` (param) | yes | User message |
| `username` (header) | yes | Conversation ID |

### Structured Output — `GET /api/structured-response`
Returns a structured `CountryCities` JSON object.

| Param | Required | Description |
|---|---|---|
| `message` | yes | e.g. `"List cities in France"` |

Additional structured output variants:
- `GET /api/structured-response2` — uses explicit `BeanOutputConverter`
- `GET /api/list-response` — returns `List<String>`
- `GET /api/map-response` — returns `Map<String, Object>`
- `GET /api/structured-list-response` — returns `List<CountryCities>`

---

## Key Components

- `TokenUsageAuditAdvisor` — logs token usage after each call
- `DocumentLoader` — loads and chunks `hr-policy.pdf` into Qdrant on startup
- `DataLoader` — (disabled) loads sample text sentences into Qdrant
- `WebSearchDocumentRetriever` — retrieves documents via Tavily web search API
