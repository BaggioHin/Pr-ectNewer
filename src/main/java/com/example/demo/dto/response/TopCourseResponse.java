package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopCourseResponse {
    private Long courseId;
    private String courseCode;
    private String courseName;
    private Long totalStudents;
}
