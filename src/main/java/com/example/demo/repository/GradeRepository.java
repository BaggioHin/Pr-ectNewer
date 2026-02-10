package com.example.demo.repository;

import com.example.demo.constant.TypeGrade;
import com.example.demo.entity.gradeAndEvaluate.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findByStudent_UserIdAndTypeGrade(Long studentId, TypeGrade typeGrade);
}
