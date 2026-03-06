package com.example.demo.repository;

import com.example.demo.entity.gradeAndEvaluate.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    boolean existsBySubjectId(Long subjectId);
    List<Exam> findByCourseClass_NameContainingIgnoreCase(String name);

    Page<Exam> findByCourseClass_IdIn(List<Long> courseClassIds, Pageable pageable);

    List<Exam> findByCourseClass_IdIn(List<Long> courseClassIds);

    @Query("""
            select e from Exam e
            where e.courseClass.id in :courseClassIds
              and exists (
                select 1 from ExamResult er
                where er.exam = e and er.student.userId = :studentId
              )
            """)
    Page<Exam> findDoneByStudent(
            @Param("courseClassIds") List<Long> courseClassIds,
            @Param("studentId") Long studentId,
            Pageable pageable
    );

    @Query("""
            select e from Exam e
            where e.courseClass.id in :courseClassIds
              and not exists (
                select 1 from ExamResult er
                where er.exam = e and er.student.userId = :studentId
              )
            """)
    Page<Exam> findNotDoneByStudent(
            @Param("courseClassIds") List<Long> courseClassIds,
            @Param("studentId") Long studentId,
            Pageable pageable
    );
}
