package com.example.demo.service.impl;

import com.example.demo.constant.FileType;
import com.example.demo.constant.NotificationRefType;
import com.example.demo.constant.NotificationType;
import com.example.demo.constant.EnrollmentStatus;
import com.example.demo.dto.request.CloudinaryUploadResult;
import com.example.demo.dto.response.DocumentResponse;
import com.example.demo.dto.response.DocumentSearchResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.Document.Document;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.CloudinaryService;
import com.example.demo.service.ai.DocumentIngestionAsyncService;
import com.example.demo.repository.projection.DocumentEmbeddingSearchRow;
import com.example.demo.service.ai.DocumentEmbeddingService;
import com.example.demo.service.k1.DocumentService;
import com.example.demo.service.notification.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final NotificationService notificationService;

    public DocumentServiceImpl(
            CloudinaryService cloudinaryService,
            DocumentRepository documentRepository,
            DocumentIngestionAsyncService documentIngestionAsyncService,
            DocumentEmbeddingService documentEmbeddingService,
            EnrollmentRepository enrollmentRepository,
            StudentRepository studentRepository,
            NotificationService notificationService
    ) {
        this.cloudinaryService = cloudinaryService;
        this.documentRepository = documentRepository;
        this.documentIngestionAsyncService = documentIngestionAsyncService;
        this.documentEmbeddingService = documentEmbeddingService;
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.notificationService = notificationService;
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
            notifyStudentsNewDocument(savedDocument);
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

    @Override
    public PageResponse<DocumentResponse> getMyDocuments(int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        Long userId = jwt.getClaim("userId");
        if (!studentRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        List<Long> courseIds = enrollmentRepository.findCourseIdsByStudentUserId(userId);
        if (courseIds.isEmpty()) {
            return new PageResponse<>(List.of(), page, size, 0, 0);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Document> pageResult = documentRepository.findByCourseIdIn(courseIds, pageable);
        List<DocumentResponse> data = pageResult.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return new PageResponse<>(
                data,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
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

    private DocumentResponse toResponse(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .description(document.getDescription())
                .fileUrl(document.getFileUrl())
                .fileType(document.getFileType())
                .courseId(document.getCourseId())
                .uploadedBy(document.getUploadedBy())
                .createdAt(document.getCreatedAt())
                .build();
    }

    private void notifyStudentsNewDocument(Document document) {
        if (document == null || document.getCourseId() == null) {
            return;
        }
        List<Long> userIds = enrollmentRepository.findStudentUserIdsByCourseIdAndStatuses(
                document.getCourseId(),
                List.of(EnrollmentStatus.STUDYING)
        );
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        String title = "Tai lieu moi";
        String docTitle = document.getTitle();
        String content = (docTitle == null || docTitle.isBlank())
                ? "Khoa hoc cua ban co tai lieu moi."
                : "Khoa hoc cua ban co tai lieu moi: " + docTitle + ".";
        notificationService.notifyUsers(
                title,
                content,
                NotificationType.DOCUMENT_UPLOADED,
                NotificationRefType.DOCUMENT,
                document.getId(),
                userIds
        );
    }
}
