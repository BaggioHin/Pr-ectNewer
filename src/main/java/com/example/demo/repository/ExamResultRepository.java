package com.example.demo.repository;

import com.example.demo.entity.gradeAndEvaluate.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    boolean existsByExam_IdAndStudent_UserId(Long examId, Long studentId);

    Optional<ExamResult> findByExam_IdAndStudent_UserId(Long examId, Long studentId);

    List<ExamResult> findByStudent_UserIdAndExam_IdIn(Long studentId, List<Long> examIds);

    Page<ExamResult> findByStudent_UserId(Long studentId, Pageable pageable);

    Page<ExamResult> findByStudent_UserIdAndExam_Id(Long studentId, Long examId, Pageable pageable);
}
