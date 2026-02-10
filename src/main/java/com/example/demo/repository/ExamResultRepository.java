package com.example.demo.repository;

import com.example.demo.entity.gradeAndEvaluate.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    boolean existsByExam_IdAndStudent_UserId(Long examId, Long studentId);
}
