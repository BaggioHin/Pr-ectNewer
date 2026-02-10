-- Run this manually after enabling the pgvector extension.
-- Choose one index type. HNSW is recommended for high recall.

-- HNSW (cosine distance)
CREATE INDEX IF NOT EXISTS document_embedding_embedding_hnsw
ON document_embedding
USING hnsw (embedding vector_cosine_ops);

-- IVFFLAT (cosine distance) - requires ANALYZE after insert
-- CREATE INDEX IF NOT EXISTS document_embedding_embedding_ivfflat
-- ON document_embedding
-- USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);
