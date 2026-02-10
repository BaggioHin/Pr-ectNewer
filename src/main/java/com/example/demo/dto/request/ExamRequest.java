package com.example.demo.dto.request;

import com.example.demo.constant.TypeGrade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamRequest {
    private Long courseClassId;
    private Long subjectId;
    private TypeGrade typeGrade;
    private LocalDate examDate;
    private Integer duration;
}
