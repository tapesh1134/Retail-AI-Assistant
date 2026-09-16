# Retail AI Assistant Demo

A small Spring AI demo for a retailer support assistant.

## Demo use cases

1. Order status
2. Returns / refunds / warranty policy
3. Product compatibility

The AI layer remains real: Spring AI + OpenAI + tool calling + PGVector RAG.

For reproducibility, the order data is stored locally in PostgreSQL instead of calling a real retailer API.

## Architecture

Web chat
  -> Spring Boot
  -> Spring AI ChatClient
      -> Order Tool -> PostgreSQL demo orders
      -> Compatibility Tool -> demo compatibility service
      -> PGVector -> policy documents
      -> OpenAI model

The streaming endpoint is:

POST /api/chat/stream

## Requirements

- Java 21
- Maven 3.9+
- Docker Desktop
- OpenAI API key

## 1. Start PostgreSQL + PGVector

```bash
docker compose up -d
```

## 2. Set API key

PowerShell:

```powershell
$env:OPENAI_API_KEY="YOUR_KEY"
```

Linux/macOS:

```bash
export OPENAI_API_KEY="YOUR_KEY"
```

You can also create a `.env` for your own shell tooling, but do not commit secrets.

## 3. Run

```bash
mvn spring-boot:run
```

Open:

http://localhost:8080/

## Demo orders

- 10001 -> SHIPPED
- 10002 -> PROCESSING
- 10003 -> DELIVERED
- 10004 -> OUT_FOR_DELIVERY

Example:

```text
Where is order 10001?
```

## Demo compatibility

```text
CHARGER-65W + LAPTOP-USB-C -> compatible
CHARGER-20W + LAPTOP-USB-C -> not sufficient
```

## Useful API calls

```bash
curl http://localhost:8080/api/orders/10001
```

Chat:

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d "{\"message\":\"Where is order 10001?\"}"
```

Streaming:

```bash
curl -N -X POST http://localhost:8080/api/chat/stream \
  -H "Content-Type: application/json" \
  -d "{\"message\":\"Can I return a product after 20 days?\"}"
```

## Performance demo

The target is:

- typed chat: under 800 ms
- voice streaming: under 300 ms per token

These are target budgets, not guaranteed values. Actual latency depends on network, model, provider, prompt size, retrieval time, and infrastructure.

Measure at least:

- request start
- policy retrieval latency
- tool latency
- time to first token (TTFT)
- total response latency
- inter-token latency

The project currently exposes Actuator metrics at:

http://localhost:8080/actuator/health

## Voice

The backend is intentionally channel-neutral. Web chat uses the streaming endpoint.

For the next demo phase, add:

Browser/Phone
  -> STT
  -> POST/stream to the same assistant
  -> TTS streaming

Do not create a second AI brain for voice. Reuse `RetailAssistantService`.

## Important demo design choice

There is no external order API in this version.

The order tool reads demo orders from PostgreSQL. This makes the demo deterministic while preserving the important AI behavior:

LLM -> tool selection -> database lookup -> tool result -> LLM response

## Suggested demo sequence

1. Ask "Where is order 10001?"
2. Show that Spring AI invokes `getOrderStatus`.
3. Ask "Can I return a product after 20 days?"
4. Show PGVector policy retrieval.
5. Ask "Is CHARGER-65W compatible with LAPTOP-USB-C?"
6. Show the compatibility tool.
7. Ask for an unknown order and show that the assistant does not invent information.
8. Show streaming response and latency measurement.

## Notes

The project uses Spring AI 1.0.9 and Java 21. If you change Spring AI versions, re-check the corresponding API and starter names.
