package com.example.demo.repository;

import com.example.demo.entity.gradeAndEvaluate.ExamQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {
    List<ExamQuestion> findByExam_Id(Long examId);
    Page<ExamQuestion> findByExam_Id(Long examId, Pageable pageable);
    long countByExam_Id(Long examId);
}
