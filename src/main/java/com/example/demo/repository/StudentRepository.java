package com.example.demo.repository;

import com.example.demo.entity.people.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {
//    Student findByFullname(String name);
    @Query("select max(s.studentCode) from Student s where s.studentCode like concat(:prefix, '%')")
    String findMaxStudentCodeByPrefix(@Param("prefix") String prefix);
}
