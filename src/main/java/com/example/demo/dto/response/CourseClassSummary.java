package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseClassSummary {
    Long id;
    String name;
    String thumbnail;
    LocalDate startDate;
    LocalDate endDate;
    String teacherName;
}
