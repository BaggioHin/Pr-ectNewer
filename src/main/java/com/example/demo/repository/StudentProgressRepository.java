package com.example.demo.repository;

import com.example.demo.entity.loginAndProcess.StudentProgress;
import com.example.demo.entity.loginAndProcess.StudentProgressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentProgressRepository extends JpaRepository<StudentProgress, StudentProgressId> {
}
