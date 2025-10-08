CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS product (
  id BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  description TEXT NOT NULL,
  metadata JSONB DEFAULT '{}'::jsonb,
  embedding vector(768)
);

-- HNSW index for cosine distance
CREATE INDEX IF NOT EXISTS idx_product_embedding_cos
  ON product USING hnsw (embedding vector_cosine_ops)
  WITH (m = 16, ef_construction = 64);

-- FTS index (English) for name + description
CREATE INDEX IF NOT EXISTS idx_product_fts ON product
USING GIN (to_tsvector('english', coalesce(name,'') || ' ' || coalesce(description,'')));

-- Optional GIN for metadata filters
CREATE INDEX IF NOT EXISTS idx_product_metadata_gin ON product USING GIN (metadata);
