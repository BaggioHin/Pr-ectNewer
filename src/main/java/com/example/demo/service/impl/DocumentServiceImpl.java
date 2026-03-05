package com.example.demo.service.impl;

import com.example.demo.constant.FileType;
import com.example.demo.dto.request.CloudinaryUploadResult;
import com.example.demo.dto.response.DocumentSearchResponse;
import com.example.demo.entity.Document.Document;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.service.CloudinaryService;
import com.example.demo.service.ai.DocumentIngestionAsyncService;
import com.example.demo.repository.projection.DocumentEmbeddingSearchRow;
import com.example.demo.service.ai.DocumentEmbeddingService;
import com.example.demo.service.k1.DocumentService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final CloudinaryService cloudinaryService;
    private final DocumentRepository documentRepository;
    private final DocumentIngestionAsyncService documentIngestionAsyncService;
    private final DocumentEmbeddingService documentEmbeddingService;

    public DocumentServiceImpl(
            CloudinaryService cloudinaryService,
            DocumentRepository documentRepository,
            DocumentIngestionAsyncService documentIngestionAsyncService,
            DocumentEmbeddingService documentEmbeddingService
    ) {
        this.cloudinaryService = cloudinaryService;
        this.documentRepository = documentRepository;
        this.documentIngestionAsyncService = documentIngestionAsyncService;
        this.documentEmbeddingService = documentEmbeddingService;
    }

    @Override
    public String uploadDocument(
            MultipartFile file,
            String title,
            String description,
            Long courseId
    ) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.DOCUMENT_FILE_REQUIRED);
        }

        String originalFilename = file.getOriginalFilename();
        FileType fileType = resolveFileType(originalFilename);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");

        CloudinaryUploadResult uploadResult = cloudinaryService.uploadDocument(file, userId.toString());

        String resolvedTitle = (title == null || title.isBlank())
                ? extractBaseName(originalFilename)
                : title.trim();

        Document document = Document.builder()
                .title(resolvedTitle)
                .description(description)
                .fileUrl(uploadResult.getUrl())
                .fileType(fileType)
                .courseId(courseId)
                .uploadedBy(userId)
                .build();

        try {
            Document savedDocument = documentRepository.save(document);
            documentIngestionAsyncService.processDocument(savedDocument.getId(), file.getBytes(), fileType);
            return "Upload successful. Document is being processed asynchronously.";
        } catch (RuntimeException exception) {
            cloudinaryService.deleteDocument(uploadResult.getPublicId());
            throw exception;
        } catch (Exception exception) {
            cloudinaryService.deleteDocument(uploadResult.getPublicId());
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    @Override
    public List<DocumentSearchResponse> searchDocuments(String query, int limit) {
        List<DocumentEmbeddingSearchRow> rows = documentEmbeddingService.search(query, limit);
        return rows.stream()
                .map(row -> DocumentSearchResponse.builder()
                        .documentId(row.getDocumentId())
                        .content(row.getContentChunk())
                        .score(row.getScore())
                        .build())
                .toList();
    }

    private FileType resolveFileType(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new AppException(ErrorCode.DOCUMENT_FILE_TYPE_NOT_SUPPORTED);
        }

        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return switch (extension) {
            case "pdf" -> FileType.PDF;
            case "docx" -> FileType.DOCX;
            default -> throw new AppException(ErrorCode.DOCUMENT_FILE_TYPE_NOT_SUPPORTED);
        };
    }

    private String extractBaseName(String filename) {
        if (filename == null || filename.isBlank()) {
            return "Document";
        }

        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex <= 0) {
            return filename;
        }

        return filename.substring(0, dotIndex);
    }
}
