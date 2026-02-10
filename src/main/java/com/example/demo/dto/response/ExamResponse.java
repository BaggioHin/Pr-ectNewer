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
public class ExamResponse {
    private Long id;
    private Long courseClassId;
    private Long subjectId;
    private TypeGrade typeGrade;
    private LocalDate examDate;
    private int duration;
}
