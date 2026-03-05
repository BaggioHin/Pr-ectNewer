package com.example.demo.service.ai;

import com.example.demo.entity.Document.Document;
import com.example.demo.entity.Document.DocumentChunk;
import com.example.demo.repository.DocumentChunkRepository;
import com.example.demo.repository.projection.DocumentEmbeddingSearchRow;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentEmbeddingService {

    private static final int DEFAULT_CHUNK_SIZE = 1000;
    private static final int DEFAULT_CHUNK_OVERLAP = 150;

    private final DocumentChunkRepository documentChunkRepository;

    public DocumentEmbeddingService(DocumentChunkRepository documentChunkRepository) {
        this.documentChunkRepository = documentChunkRepository;
    }

    public void indexDocument(Document document, String fullText) {

        if (document == null || document.getId() == null) {
            throw new IllegalArgumentException("documentId is required");
        }
        if (fullText == null || fullText.trim().isEmpty()) {
            throw new IllegalArgumentException("fullText is required");
        }

        List<String> chunks = chunkText(fullText, DEFAULT_CHUNK_SIZE, DEFAULT_CHUNK_OVERLAP);

        saveChunks(document, chunks);
    }

    @Transactional
    protected void saveChunks(Document document,
                              List<String> chunks) {

        documentChunkRepository.deleteByDocument_Id(document.getId());

        List<DocumentChunk> entities = new ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {
            entities.add(DocumentChunk.builder()
                    .document(document)
                    .content(chunks.get(i))
                    .chunkIndex(i)
                    .build());
        }

        documentChunkRepository.saveAll(entities);
    }

    public List<DocumentEmbeddingSearchRow> search(String query, int limit) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("query is required");
        }
        int safeLimit = Math.max(1, limit);
        return documentChunkRepository.searchFullText(query, safeLimit);
    }

    private List<String> chunkText(String text, int chunkSize, int overlap) {
        String normalized = text.replaceAll("\\s+", " ").trim();
        if (normalized.isEmpty()) {
            return List.of();
        }

        int safeChunkSize = Math.max(200, chunkSize);
        int safeOverlap = Math.max(0, Math.min(overlap, safeChunkSize - 1));

        List<String> chunks = new ArrayList<>();
        int start = 0;
        int length = normalized.length();
        while (start < length) {
            int end = Math.min(length, start + safeChunkSize);
            String chunk = normalized.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }

            if (end >= length) {
                break;
            }
            start = end - safeOverlap;
        }
        return chunks;
    }
}
