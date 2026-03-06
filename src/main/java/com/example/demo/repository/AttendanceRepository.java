package com.example.demo.repository;

import com.example.demo.entity.classAndLearn.Attendance;
import com.example.demo.entity.classAndLearn.AttendanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.demo.constant.StatusAttendance;
import com.example.demo.constant.StatusClassSession;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, AttendanceId> {
    @Query("""
            select count(a) from Attendance a
            where a.student.userId = :studentId
              and a.classSession.classSchedule.courseClass.id = :courseClassId
              and a.classSession.statusClassSession = :sessionStatus
              and a.statusAttendance in :attendanceStatuses
            """)
    long countAttendedSessions(
            @Param("studentId") Long studentId,
            @Param("courseClassId") Long courseClassId,
            @Param("sessionStatus") StatusClassSession sessionStatus,
            @Param("attendanceStatuses") List<StatusAttendance> attendanceStatuses
    );
}
