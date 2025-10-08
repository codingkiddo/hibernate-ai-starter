#!/usr/bin/env bash
set -euo pipefail

API="${API:-http://localhost:8080}"

echo ">>> Creating sample products"
curl -s -X POST "$API/api/products" -H 'Content-Type: application/json' -d '{"name":"Apple MacBook Air M2","description":"13-inch ultraportable laptop ideal for travel and development.","metadata":"{\"category\":\"laptop\",\"brand\":\"Apple\"}"}' | jq -r '.id' || true
curl -s -X POST "$API/api/products" -H 'Content-Type: application/json' -d '{"name":"Dell XPS 13","description":"Premium ultrabook for productivity.","metadata":"{\"category\":\"laptop\",\"brand\":\"Dell\"}"}' | jq -r '.id' || true

echo ">>> Vector search"
curl -s "$API/api/products/search?q=ultraportable%20laptop&k=5" | jq '.[] | {id,name}'

echo ">>> Hybrid search (FTS + vector)"
curl -s "$API/api/products/search/hybrid?q=ultraportable%20laptop&k=5&wf=0.6&wv=0.4" | jq '.[] | {id,name}'
