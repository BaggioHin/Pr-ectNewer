CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE document_embedding (
                                    id BIGSERIAL PRIMARY KEY,
                                    content_chunk TEXT,
                                    document_id BIGINT NOT NULL,
                                    embedding VECTOR(1536) NOT NULL
);
