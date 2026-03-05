package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.AdminStatsResponse;
import com.example.demo.dto.response.AuditLogResponse;
import com.example.demo.dto.response.MonthlyStatsResponse;
import com.example.demo.dto.response.MonthlyRevenueStatsResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.SalerMonthlyRevenueResponse;
import com.example.demo.dto.response.SalerRevenueStatsResponse;
import com.example.demo.dto.response.SalerTransactionResponse;
import com.example.demo.dto.response.TopCourseResponse;
import com.example.demo.dto.response.TopSalerResponse;
import com.example.demo.service.k1.AdminStatsService;
import com.example.demo.service.k1.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    AdminStatsService adminStatsService;
    @Autowired
    AuditLogService auditLogService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<AdminStatsResponse> getStats() {
        return ApiResponse.<AdminStatsResponse>builder()
                .result(adminStatsService.getStats())
                .build();
    }

    @GetMapping("/stats/monthly")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<MonthlyStatsResponse> getMonthlyStats(
            @RequestParam int year,
            @RequestParam int month) {
        return ApiResponse.<MonthlyStatsResponse>builder()
                .result(adminStatsService.getMonthlyStats(year, month))
                .build();
    }

    @GetMapping("/stats/revenue/latest")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<java.util.List<MonthlyRevenueStatsResponse>> getLatestMonthlyRevenueStats() {
        return ApiResponse.<java.util.List<MonthlyRevenueStatsResponse>>builder()
                .result(adminStatsService.getLatestMonthlyRevenueStats())
                .build();
    }

    @GetMapping("/stats/courses/top")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<java.util.List<TopCourseResponse>> getTopCourses() {
        return ApiResponse.<java.util.List<TopCourseResponse>>builder()
                .result(adminStatsService.getTopCourses())
                .build();
    }

    @GetMapping("/stats/salers/top")
    @PreAuthorize("hasAnyRole('ADMIN','SALER')")
    ApiResponse<java.util.List<TopSalerResponse>> getTopSalers() {
        return ApiResponse.<java.util.List<TopSalerResponse>>builder()
                .result(adminStatsService.getTopSalers())
                .build();
    }

    @GetMapping("/stats/salers")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<java.util.List<SalerRevenueStatsResponse>> getAllSalerRevenueStats() {
        return ApiResponse.<java.util.List<SalerRevenueStatsResponse>>builder()
                .result(adminStatsService.getAllSalerRevenueStats())
                .build();
    }

    @GetMapping("/stats/salers/{salerId}")
    @PreAuthorize("hasAnyRole('ADMIN','SALER')")
    ApiResponse<SalerRevenueStatsResponse> getSalerRevenueStats(@PathVariable Long salerId) {
        return ApiResponse.<SalerRevenueStatsResponse>builder()
                .result(adminStatsService.getSalerRevenueStats(salerId))
                .build();
    }

    @GetMapping("/stats/salers/{salerId}/monthly")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<SalerMonthlyRevenueResponse> getSalerMonthlyRevenue(
            @PathVariable Long salerId,
            @RequestParam int year,
            @RequestParam int month) {
        return ApiResponse.<SalerMonthlyRevenueResponse>builder()
                .result(adminStatsService.getSalerMonthlyRevenue(salerId, year, month))
                .build();
    }

    @GetMapping("/stats/salers/{salerId}/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<PageResponse<SalerTransactionResponse>> getSalerTransactions(
            @PathVariable Long salerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<SalerTransactionResponse>>builder()
                .result(adminStatsService.getSalerTransactions(salerId, page, size))
                .build();
    }

    @GetMapping("/audits")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<PageResponse<AuditLogResponse>> getAudits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<AuditLogResponse>>builder()
                .result(auditLogService.getAudits(page, size))
                .build();
    }
}
