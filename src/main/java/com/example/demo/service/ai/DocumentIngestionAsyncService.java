package com.example.demo.service.ai;

import com.example.demo.constant.FileType;
import com.example.demo.entity.Document.Document;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.DocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DocumentIngestionAsyncService {

    private final DocumentRepository documentRepository;
    private final DocumentTextExtractionService documentTextExtractionService;
    private final DocumentEmbeddingService documentEmbeddingService;

    public DocumentIngestionAsyncService(
            DocumentRepository documentRepository,
            DocumentTextExtractionService documentTextExtractionService,
            DocumentEmbeddingService documentEmbeddingService
    ) {
        this.documentRepository = documentRepository;
        this.documentTextExtractionService = documentTextExtractionService;
        this.documentEmbeddingService = documentEmbeddingService;
    }

    @Async
    public void processDocument(Long documentId, byte[] fileBytes, FileType fileType) {
        try {
            Document document = documentRepository.findById(documentId)
                    .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

            String fullText = documentTextExtractionService.extractText(fileBytes, fileType);
            if (fullText == null || fullText.trim().isEmpty()) {
                log.warn("Document {} extracted empty content", documentId);
                return;
            }

            documentEmbeddingService.indexDocument(document, fullText);
            log.info("Document {} indexed successfully", documentId);
        } catch (Exception e) {
            log.error("Async document processing failed for documentId={}", documentId, e);
        }
    }
}
