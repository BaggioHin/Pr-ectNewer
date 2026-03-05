package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyStatsResponse {
    private Integer year;
    private Integer month;
    private Long totalRevenue;
    private Long newStudentCount;
    private Double closeRate;
    private Double postponeRate;
}
