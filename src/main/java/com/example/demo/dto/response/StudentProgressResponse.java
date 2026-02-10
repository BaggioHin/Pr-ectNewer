package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProgressResponse {
    private Long studentId;
    private Long courseClassId;
    private Double completionPercent;
    private LocalDateTime updatedAt;
}
