package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.DocumentSearchResponse;
import com.example.demo.service.k1.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/document")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Operation(summary = "Upload document to Cloudinary")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    ApiResponse<String> uploadDocument(
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Long courseId
    ) {
        return ApiResponse.<String>builder()
                .result(documentService.uploadDocument(file, title, description, courseId))
                .build();
    }

    @Operation(summary = "Search documents (full-text)")
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    ApiResponse<List<DocumentSearchResponse>> searchDocuments(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ApiResponse.<List<DocumentSearchResponse>>builder()
                .result(documentService.searchDocuments(query, limit))
                .build();
    }
}
