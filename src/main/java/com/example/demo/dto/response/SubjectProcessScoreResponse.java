package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectProcessScoreResponse {
    private Long subjectId;
    private String subjectName;
    private Double midtermScore;
    private Double finalScore;
    private boolean quizCompleted;
    private double bonus;
    private Double totalScore;
}
