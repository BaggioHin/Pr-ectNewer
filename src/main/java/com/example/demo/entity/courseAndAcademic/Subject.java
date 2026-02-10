package com.example.demo.entity.courseAndAcademic;

import com.example.demo.entity.classAndLearn.ClassSchedule;
import com.example.demo.entity.classAndLearn.CourseClass;
import com.example.demo.entity.gradeAndEvaluate.Exam;
import com.example.demo.entity.loginAndProcess.StudentProgress;
import com.example.demo.entity.people.Teacher;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(unique = true, length = 50)
    private String code;

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 2000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @NotNull
    private Course course;

    @OneToMany(mappedBy = "subject")
    private List<Exam> exams;

}



