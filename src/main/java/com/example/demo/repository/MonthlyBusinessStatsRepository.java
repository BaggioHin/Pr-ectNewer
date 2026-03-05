package com.example.demo.repository;

import com.example.demo.entity.statistics.MonthlyBusinessStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonthlyBusinessStatsRepository extends JpaRepository<MonthlyBusinessStats, Long> {
    Optional<MonthlyBusinessStats> findByStatYearAndStatMonth(Integer statYear, Integer statMonth);
}
