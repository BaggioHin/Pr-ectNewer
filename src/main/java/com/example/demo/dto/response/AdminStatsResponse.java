package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {
    private long userCount;
    private long courseClassCount;
    private long courseCount;
    private long documentCount;
    private long studentCount;
    private long teacherCount;
    private long adminCount;
    private long salerCount;
}
