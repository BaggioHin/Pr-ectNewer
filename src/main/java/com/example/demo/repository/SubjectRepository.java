package com.example.demo.repository;

import com.example.demo.entity.courseAndAcademic.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject,Long> {
    Optional<Subject> findByNameIgnoreCase(String name);
    List<Subject> findByNameContainingIgnoreCase(String name);
    List<Subject> findByCourse_Id(Long courseId);
    Page<Subject> findByCourse_Id(Long courseId, Pageable pageable);
    @Query("select max(s.code) from Subject s where s.code like concat(:prefix, '%')")
    String findMaxCodeByPrefix(@Param("prefix") String prefix);
}
