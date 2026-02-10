package com.example.demo.repository.projection;

public interface DocumentEmbeddingSearchRow {
    Long getDocumentId();
    String getContentChunk();
    Double getScore();
}
