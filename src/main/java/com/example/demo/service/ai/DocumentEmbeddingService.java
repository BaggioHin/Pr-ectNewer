package com.example.demo.service.ai;

import com.example.demo.entity.Document.DocumentContent;
import com.example.demo.entity.Document.DocumentEmbedding;
import com.example.demo.repository.DocumentContentRepository;
import com.example.demo.repository.DocumentEmbeddingRepository;
import com.example.demo.repository.projection.DocumentEmbeddingSearchRow;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentEmbeddingService {

    private static final int DEFAULT_CHUNK_SIZE = 1000;
    private static final int DEFAULT_CHUNK_OVERLAP = 150;

    @Autowired
    private DocumentContentRepository documentContentRepository;

    @Autowired
    private DocumentEmbeddingRepository documentEmbeddingRepository;

    @Autowired
    private EmbeddingProvider embeddingProvider;

    @Transactional
    public void indexDocument(Long documentId, String fullText) {
        if (documentId == null) {
            throw new IllegalArgumentException("documentId is required");
        }
        if (fullText == null || fullText.trim().isEmpty()) {
            throw new IllegalArgumentException("fullText is required");
        }

        documentContentRepository.deleteByDocumentId(documentId);
        documentEmbeddingRepository.deleteByDocumentId(documentId);

        List<String> chunks = chunkText(fullText, DEFAULT_CHUNK_SIZE, DEFAULT_CHUNK_OVERLAP);
        int index = 0;
        for (String chunk : chunks) {
            DocumentContent content = DocumentContent.builder()
                    .documentId(documentId)
                    .content(chunk)
                    .chunkIndex(index)
                    .build();
            documentContentRepository.save(content);

            float[] embedding = embeddingProvider.embed(chunk);

            DocumentEmbedding documentEmbedding = DocumentEmbedding.builder()
                    .documentId(documentId)
                    .embedding(embedding)
                    .contentChunk(chunk)
                    .build();
            documentEmbeddingRepository.save(documentEmbedding);
            index++;
        }
    }

    public List<DocumentEmbeddingSearchRow> search(String query, int limit) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("query is required");
        }
        int safeLimit = Math.max(1, limit);
        float[] queryEmbedding = embeddingProvider.embed(query);
        return documentEmbeddingRepository.searchSimilar(queryEmbedding, safeLimit);
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
