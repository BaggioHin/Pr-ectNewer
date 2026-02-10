package com.example.demo.repository;

import com.example.demo.entity.classAndLearn.ClassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {
}
