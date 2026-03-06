package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassProgressResponse {
    private Long courseClassId;
    private int completedSessions;
    private int totalSessions;
    private double progressPercent;
}
