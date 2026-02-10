package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeachingAssignmentSummary {
    Long id;
    String name;
    String thumbnail;
    LocalDateTime startDate;
    LocalDateTime endDate;
    String teacherName;
}
