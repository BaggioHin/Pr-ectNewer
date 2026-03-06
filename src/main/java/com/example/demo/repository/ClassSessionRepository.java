package com.example.demo.repository;

import com.example.demo.entity.classAndLearn.ClassSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.demo.constant.StatusClassSession;
import java.util.List;

@Repository
public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {
    long countByClassSchedule_CourseClass_Id(Long courseClassId);

    long countByClassSchedule_CourseClass_IdAndStatusClassSession(
            Long courseClassId,
            StatusClassSession statusClassSession
    );

    @Query("""
            select count(cs) from ClassSession cs
            where cs.classSchedule.courseClass.id = :courseClassId
              and cs.statusClassSession <> :excluded
            """)
    long countByCourseClassIdAndStatusNot(
            @Param("courseClassId") Long courseClassId,
            @Param("excluded") StatusClassSession excluded
    );

    @Query("""
            select cs from ClassSession cs
            where cs.classSchedule.courseClass.id in :courseClassIds
            """)
    List<ClassSession> findByCourseClassIds(@Param("courseClassIds") List<Long> courseClassIds);

    @Query("""
            select cs from ClassSession cs
            where cs.classSchedule.courseClass.id = :courseClassId
              and cs.makeup = true
            order by cs.date asc, cs.startTime asc
            """)
    List<ClassSession> findMakeupSessionsByCourseClassId(@Param("courseClassId") Long courseClassId);
}
