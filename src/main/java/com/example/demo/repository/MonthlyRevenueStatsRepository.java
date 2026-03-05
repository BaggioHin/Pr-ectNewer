package com.example.demo.repository;

import com.example.demo.entity.statistics.MonthlyRevenueStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonthlyRevenueStatsRepository extends JpaRepository<MonthlyRevenueStats, Long> {
    Optional<MonthlyRevenueStats> findByStatYearAndStatMonth(Integer statYear, Integer statMonth);

    java.util.List<MonthlyRevenueStats> findTop5ByOrderByStatYearDescStatMonthDesc();
}
