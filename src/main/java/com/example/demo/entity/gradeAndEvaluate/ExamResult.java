package com.example.demo.entity.gradeAndEvaluate;

import com.example.demo.entity.people.Student;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    @NotNull
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @NotNull
    private Student student;

    @ManyToOne
    @JoinColumn(name = "grade_id")
    private Grade grade;

    @Min(0)
    private double score;
}

