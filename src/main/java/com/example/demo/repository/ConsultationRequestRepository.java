package com.example.demo.repository;

import com.example.demo.constant.ConsultationStatus;
import com.example.demo.entity.sales.ConsultationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ConsultationRequestRepository extends JpaRepository<ConsultationRequest, Long> {
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByCreatedAtBetweenAndStatus(LocalDateTime start, LocalDateTime end, ConsultationStatus status);
}
