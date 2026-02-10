package com.example.demo.repository;

import com.example.demo.entity.loginAndProcess.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment,Long> {
    boolean existsByStudent_UserIdAndCourseClass_Id(Long userId, Long courseClassId);

    long countByCourseClass_Id(Long courseClassId);

    java.util.List<Enrollment> findByCourseClass_Id(Long courseClassId);
}
