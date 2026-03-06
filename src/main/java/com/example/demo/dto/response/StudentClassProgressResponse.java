package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentClassProgressResponse {
    private Long courseClassId;
    private Long studentId;
    private int attendedSessions;
    private int completedSessions;
    private double progressPercent;
}
