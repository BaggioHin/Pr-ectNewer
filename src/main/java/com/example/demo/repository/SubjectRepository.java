package com.example.demo.repository;

import com.example.demo.entity.courseAndAcademic.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject,Long> {
    Optional<Subject> findByNameIgnoreCase(String name);
    List<Subject> findByNameContainingIgnoreCase(String name);
}
