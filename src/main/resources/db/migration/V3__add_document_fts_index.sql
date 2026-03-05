CREATE INDEX IF NOT EXISTS idx_document_fts
    ON document USING GIN (to_tsvector('simple', coalesce(title, '') || ' ' || coalesce(description, '')));
