CREATE TABLE IF NOT EXISTS document_chunk (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    chunk_index INTEGER NOT NULL,
    CONSTRAINT fk_document_chunk_document
        FOREIGN KEY (document_id) REFERENCES document(id)
);

CREATE INDEX IF NOT EXISTS idx_document_id
    ON document_chunk (document_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_document_chunk_doc_idx
    ON document_chunk (document_id, chunk_index);

CREATE INDEX IF NOT EXISTS idx_document_chunk_fts
    ON document_chunk USING GIN (to_tsvector('simple', content));
