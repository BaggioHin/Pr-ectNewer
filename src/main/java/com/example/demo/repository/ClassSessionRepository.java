package com.example.demo.repository;

import com.example.demo.entity.classAndLearn.ClassSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {
}
