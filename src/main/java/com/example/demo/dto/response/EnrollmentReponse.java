package com.example.demo.dto.response;

import com.example.demo.constant.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentReponse {
    Long id;
    EnrollmentStatus status;
    LocalDateTime enrolledAt;
    LocalDateTime endAt;
    CourseClassSummary courseClass;
}
