package com.example.demo.repository;

import com.example.demo.entity.classAndLearn.Attendance;
import com.example.demo.entity.classAndLearn.AttendanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, AttendanceId> {
}
