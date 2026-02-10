package com.example.demo.entity.classAndLearn;

import com.example.demo.constant.StatusCourse;
import com.example.demo.entity.courseAndAcademic.Course;
import com.example.demo.entity.courseAndAcademic.Subject;
import com.example.demo.entity.gradeAndEvaluate.Exam;
import com.example.demo.entity.loginAndProcess.Enrollment;
import com.example.demo.entity.loginAndProcess.StudentProgress;
import com.example.demo.entity.people.Teacher;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(unique = true, length = 50)
    private String classCode;

    @NotBlank
    @Size(max = 255)
    private String name;

    private LocalDateTime startDay;
    private LocalDateTime endDay;

    @NotNull
    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StatusCourse statusCourse;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @NotNull
    private Course course;

    @OneToOne(mappedBy = "courseClass", cascade = CascadeType.ALL)
    private ClassSchedule schedule;

//    enrollment
    @OneToMany(mappedBy = "courseClass", cascade = CascadeType.ALL)
    private List<Enrollment> enrollments;
//    teachingAssignment
    @OneToMany(mappedBy = "courseClass", cascade = CascadeType.ALL)
    private List<TeachingAssignment> teachingAssignments;
//    exam
    @OneToMany(mappedBy = "courseClass", cascade = CascadeType.ALL)
    private List<Exam> exams;

//    studentProgress
    @OneToMany(mappedBy = "courseClass", cascade = CascadeType.ALL)
    private List<StudentProgress> studentProgresses;
}




