package com.example.demo.repository;

import com.example.demo.entity.people.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    @Query("select max(t.teacherCode) from Teacher t where t.teacherCode like concat(:prefix, '%')")
    String findMaxTeacherCodeByPrefix(@Param("prefix") String prefix);
}
