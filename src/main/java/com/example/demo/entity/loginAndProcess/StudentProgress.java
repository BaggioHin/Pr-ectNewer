package com.example.demo.entity.loginAndProcess;

import com.example.demo.entity.classAndLearn.CourseClass;
import com.example.demo.entity.people.Student;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentProgress {

    @EmbeddedId
    @NotNull
    private StudentProgressId id;

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    @NotNull
    private Student student;

    @ManyToOne
    @MapsId("courseClassId")
    @JoinColumn(name = "course_class_id")
    @NotNull
    private CourseClass courseClass;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Double completionPercent;
    private LocalDateTime updatedAt;
}

