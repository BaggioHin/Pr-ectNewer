package com.example.demo.repository;

import com.example.demo.controller.CourseClassController;
import com.example.demo.entity.classAndLearn.CourseClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface CourseClassRepository extends JpaRepository<CourseClass,Long> {
    List<CourseClass> findByName(String name);
//    Page<CourseClass>
    Page<CourseClass> findByCourse_Id(Long courseId, Pageable pageable);
    @Query("select max(c.classCode) from CourseClass c where c.classCode like concat(:prefix, '%')")
    String findMaxClassCodeByPrefix(@Param("prefix") String prefix);
}
