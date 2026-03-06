package com.example.demo.dto.response;

import com.example.demo.constant.TypeGrade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultResponse {
    private Long id;
    private Long examId;
    private Long studentId;
    private TypeGrade typeGrade;
    private double score;
    private LocalDate examDate;
    private int duration;
    private Long subjectId;
    private String subjectName;
    private Long courseClassId;
    private String courseClassName;
    private Long courseId;
    private String courseName;
}
