package com.example.demo.dto.response;

import com.example.demo.constant.TypeGrade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamSubmitResponse {
    private Long examId;
    private Long studentId;
    private TypeGrade typeGrade;
    private int totalQuestions;
    private int correctAnswers;
    private double score;
}
