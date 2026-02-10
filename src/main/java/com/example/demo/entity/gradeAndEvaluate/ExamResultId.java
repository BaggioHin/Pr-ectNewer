package com.example.demo.entity.gradeAndEvaluate;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

@Embeddable
public class ExamResultId implements Serializable {

    @NotNull
    private Long examId;
    @NotNull
    private Long studentId;

    // equals() & hashCode() BẮT BUỘC
}

