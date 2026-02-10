package com.example.demo.repository;


import com.example.demo.entity.classAndLearn.TeachingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeachingAssignmentRepository extends JpaRepository<TeachingAssignment,Long> {
    boolean existsByTeacher_UserIdAndCourseClass_Id(Long teacherId, Long courseClassId);

    java.util.List<TeachingAssignment> findByCourseClass_Id(Long courseClassId);
}
