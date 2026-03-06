package com.example.demo.dto.response;

import com.example.demo.constant.FileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private Long id;
    private String title;
    private String description;
    private String fileUrl;
    private FileType fileType;
    private Long courseId;
    private Long uploadedBy;
    private LocalDateTime createdAt;
}
