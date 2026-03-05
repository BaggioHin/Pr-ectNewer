package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalerRevenueStatsResponse {
    private Long salerId;
    private Long totalRevenue;
    private Long totalDeals;
}
