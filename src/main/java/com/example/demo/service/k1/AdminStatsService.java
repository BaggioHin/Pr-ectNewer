package com.example.demo.service.k1;

import com.example.demo.dto.response.AdminStatsResponse;
import com.example.demo.dto.response.MonthlyRevenueStatsResponse;
import com.example.demo.dto.response.MonthlyStatsResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.SalerMonthlyRevenueResponse;
import com.example.demo.dto.response.SalerRevenueStatsResponse;
import com.example.demo.dto.response.SalerTransactionResponse;
import com.example.demo.dto.response.TopCourseResponse;
import com.example.demo.dto.response.TopSalerResponse;

import java.time.LocalDateTime;

public interface AdminStatsService {
    AdminStatsResponse getStats();

    MonthlyStatsResponse getMonthlyStats(int year, int month);

    void refreshMonthlyStats(LocalDateTime eventTime);

    java.util.List<MonthlyRevenueStatsResponse> getLatestMonthlyRevenueStats();

    java.util.List<TopCourseResponse> getTopCourses();

    java.util.List<TopSalerResponse> getTopSalers();

    java.util.List<SalerRevenueStatsResponse> getAllSalerRevenueStats();

    SalerRevenueStatsResponse getSalerRevenueStats(Long salerId);

    SalerMonthlyRevenueResponse getSalerMonthlyRevenue(Long salerId, int year, int month);

    PageResponse<SalerTransactionResponse> getSalerTransactions(Long salerId, int page, int size);
}
