package com.example.demo.entity.loginAndProcess;

import com.example.demo.constant.CreateType;
import com.example.demo.constant.EnrollmentStatus;
import com.example.demo.entity.classAndLearn.CourseClass;
import com.example.demo.entity.courseAndAcademic.Course;
import com.example.demo.entity.people.Student;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "enrollments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"student_id", "class_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @NotNull
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    @NotNull
    private CourseClass courseClass;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EnrollmentStatus status;

    private LocalDateTime enrolledAt;
    private LocalDateTime endAt;
    private String createById;
    private LocalDate updatedAt;
}


