package com.example.demo.service.k1;

import com.example.demo.dto.response.DocumentSearchResponse;
import com.example.demo.dto.response.DocumentResponse;
import com.example.demo.dto.response.PageResponse;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    String uploadDocument(
            MultipartFile file,
            String title,
            String description,
            Long courseId
    );

    List<DocumentSearchResponse> searchDocuments(String query, int limit);

    PageResponse<DocumentResponse> getMyDocuments(int page, int size);
}
