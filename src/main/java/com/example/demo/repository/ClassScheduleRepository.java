package com.example.demo.repository;

import com.example.demo.entity.classAndLearn.ClassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {
    ClassSchedule findByCourseClass_Id(Long courseClassId);
    List<ClassSchedule> findByCourseClass_IdIn(List<Long> courseClassIds);
}
