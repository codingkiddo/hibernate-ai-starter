# Hibernate ORM + AI (pgvector) — Spring Boot 3 / Java 21

Minimal, production‑ready starter that combines Hibernate ORM with AI embeddings for semantic search using PostgreSQL + pgvector.

## Quick start
1. Start Postgres with pgvector:
   ```bash
   docker compose up -d
   ```
2. Run Ollama locally and pull the embedding model:
   ```bash
   ollama pull nomic-embed-text
   ```
3. Build & run:
   ```bash
   mvn -q -DskipTests spring-boot:run
   ```

### API
- `POST /api/products` — persist + auto-embed
- `GET /api/products/search?q=...&k=10` — semantic search
- `GET /api/products/search/hybrid?q=...&k=10&wf=0.6&wv=0.4` — hybrid FTS + vector

## OpenAI toggle
Set provider to OpenAI in `application.yml` and provide your API key:
```yaml
hibai:
  embeddings:
    provider: openai
    model: text-embedding-3-small
    openai:
      base-url: https://api.openai.com/v1
      api-key: ${OPENAI_API_KEY}
```
Export the key and run:
```bash
export OPENAI_API_KEY=sk-...
```

## Flyway migrations
SQL lives under `src/main/resources/db/migration` and runs automatically on startup.

## Testcontainers
Run integration tests against a real Postgres+pgvector:
```bash
mvn -q -DskipTests=false test
```

## Demo script
With the app running (and DB up), execute:
```bash
scripts/demo.sh
```
Requires `jq` for pretty-printing.
