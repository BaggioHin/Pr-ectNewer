package com.example.demo.repository;

import com.example.demo.entity.courseAndAcademic.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByNameIgnoreCase(String name);
    @Query("select max(c.code) from Course c where c.code like concat(:prefix, '%')")
    String findMaxCodeByPrefix(@Param("prefix") String prefix);
}
