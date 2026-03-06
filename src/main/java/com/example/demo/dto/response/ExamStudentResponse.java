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
public class ExamStudentResponse {
    private Long id;
    private Long courseClassId;
    private String courseClassName;
    private Long courseId;
    private String courseName;
    private Long subjectId;
    private String subjectName;
    private TypeGrade typeGrade;
    private LocalDate examDate;
    private int duration;
    private boolean done;
    private Double score;
}
