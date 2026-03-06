package com.example.demo.repository;

import com.example.demo.constant.EnrollmentStatus;
import com.example.demo.entity.loginAndProcess.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment,Long> {
    boolean existsByStudent_UserIdAndCourseClass_Id(Long userId, Long courseClassId);

    long countByCourseClass_Id(Long courseClassId);

    java.util.List<Enrollment> findByCourseClass_Id(Long courseClassId);

    java.util.List<Enrollment> findByStudent_UserId(Long userId);

    @Query("select distinct e.courseClass.id from Enrollment e where e.student.userId = :userId")
    java.util.List<Long> findCourseClassIdsByStudentUserId(@Param("userId") Long userId);

    @Query("""
            select distinct e.courseClass.id
            from Enrollment e
            where e.student.userId = :userId
              and e.courseClass.course.id = :courseId
            """)
    java.util.List<Long> findCourseClassIdsByStudentUserIdAndCourseId(
            @Param("userId") Long userId,
            @Param("courseId") Long courseId
    );

    @Query("""
            select distinct e.courseClass.course.id
            from Enrollment e
            where e.student.userId = :userId
            """)
    java.util.List<Long> findCourseIdsByStudentUserId(@Param("userId") Long userId);

    @Query("""
            select distinct e.student.userId
            from Enrollment e
            where e.courseClass.id = :courseClassId
              and e.status in :statuses
            """)
    java.util.List<Long> findStudentUserIdsByCourseClassIdAndStatuses(
            @Param("courseClassId") Long courseClassId,
            @Param("statuses") java.util.List<EnrollmentStatus> statuses
    );

    @Query("""
            select distinct e.student.userId
            from Enrollment e
            where e.courseClass.course.id = :courseId
              and e.status in :statuses
            """)
    java.util.List<Long> findStudentUserIdsByCourseIdAndStatuses(
            @Param("courseId") Long courseId,
            @Param("statuses") java.util.List<EnrollmentStatus> statuses
    );

    long countByEnrolledAtBetween(LocalDateTime start, LocalDateTime end);

    long countByUpdatedAtBetweenAndStatus(java.time.LocalDate start, java.time.LocalDate end, EnrollmentStatus status);

    @Query("""
            select coalesce(sum(c.price), 0)
            from Enrollment e
            join e.courseClass cc
            join cc.course c
            where e.enrolledAt >= :start
              and e.enrolledAt < :end
              and e.status <> :excludedStatus
            """)
    long sumRevenueByEnrolledAtBetweenExcludingStatus(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("excludedStatus") EnrollmentStatus excludedStatus
    );
}
