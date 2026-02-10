package com.example.demo.dto.response;

import com.example.demo.constant.TeachingAssignmentStatus;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeachingAssignmentResponse {
    Long id;
    TeachingAssignmentStatus status;
    LocalDateTime enrolledAt;
    LocalDateTime endAt;
    TeachingAssignmentSummary teachingAssignment;
}
