package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseFinalScoreResponse {
    private Long courseId;
    private String courseName;
    private Double averageScore;
    private int subjectCount;
    private int computedCount;
}
