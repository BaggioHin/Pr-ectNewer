package com.example.demo.repository;

import com.example.demo.controller.CourseClassController;
import com.example.demo.entity.classAndLearn.CourseClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseClassRepository extends JpaRepository<CourseClass,Long> {
    List<CourseClass> findByName(String name);
//    Page<CourseClass>
}
