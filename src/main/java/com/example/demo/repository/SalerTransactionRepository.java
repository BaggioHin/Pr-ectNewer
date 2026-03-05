package com.example.demo.repository;

import com.example.demo.entity.sales.Saler;
import com.example.demo.entity.sales.SalerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface SalerTransactionRepository extends JpaRepository<SalerTransaction, Long> {
    List<SalerTransaction> findAllBySalerOrderByOccurredAtDesc(Saler saler);

    List<SalerTransaction> findAllBySaler_IdOrderByOccurredAtDesc(Long salerId);

    List<SalerTransaction> findAllBySaler_IdAndOccurredAtBetweenOrderByOccurredAtDesc(
            Long salerId, LocalDateTime start, LocalDateTime end);

    Page<SalerTransaction> findAllBySaler_IdOrderByOccurredAtDesc(Long salerId, Pageable pageable);

    long countBySaler_Id(Long salerId);

    @Query(
            value = """
                    select * from (
                        select distinct on (student_id, course_name) *
                        from saler_transactions
                        where saler_id = :salerId
                        order by student_id, course_name, occurred_at desc, id desc
                    ) t
                    order by t.occurred_at desc
                    """,
            countQuery = """
                    select count(*) from (
                        select 1
                        from saler_transactions
                        where saler_id = :salerId
                        group by student_id, course_name
                    ) s
                    """,
            nativeQuery = true
    )
    Page<SalerTransaction> findDistinctLatestBySalerId(
            @Param("salerId") Long salerId,
            Pageable pageable
    );

    @Query("select coalesce(sum(t.amount), 0) from SalerTransaction t " +
            "where t.saler.id = :salerId and t.occurredAt >= :start and t.occurredAt < :end")
    Long sumAmountBySalerIdAndOccurredAtBetween(
            @Param("salerId") Long salerId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
