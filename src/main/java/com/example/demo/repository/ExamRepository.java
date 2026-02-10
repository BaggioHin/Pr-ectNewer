package com.example.demo.repository;

import com.example.demo.entity.gradeAndEvaluate.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    boolean existsBySubjectId(Long subjectId);
    List<Exam> findByCourseClass_NameContainingIgnoreCase(String name);
}
