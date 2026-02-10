package com.example.demo.entity.classAndLearn;

import com.example.demo.constant.CreateType;
import com.example.demo.constant.TeachingAssignmentStatus;
import com.example.demo.entity.gradeAndEvaluate.Exam;
import com.example.demo.entity.people.Student;
import com.example.demo.entity.people.Teacher;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeachingAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    @NotNull
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_class_id")
    @NotNull
    private CourseClass courseClass;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TeachingAssignmentStatus status;

    private LocalDateTime enrolledAt;
    private LocalDateTime endAt;
    private CreateType createBy;
    private LocalDateTime updatedAt;

//    @ManyToOne
//    @JoinColumn(name = "exam_id")
//    private Exam exam;
}

