package com.example.demo.repository;

import com.example.demo.entity.sales.Saler;
import com.example.demo.entity.statistics.SalerRevenueStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalerRevenueStatsRepository extends JpaRepository<SalerRevenueStats, Long> {
    Optional<SalerRevenueStats> findBySaler(Saler saler);
    Optional<SalerRevenueStats> findBySaler_Id(Long salerId);

    @Query(
            value = """
                    select srs.*
                    from saler_revenue_stats srs
                    join saler s on srs.saler_id = s.id
                    where s.user_id = :userId
                    """,
            nativeQuery = true
    )
    Optional<SalerRevenueStats> findByUserId(@Param("userId") Long userId);

    List<SalerRevenueStats> findAllByOrderByTotalRevenueDesc();

    List<SalerRevenueStats> findTop3ByOrderByTotalRevenueDesc();
}
