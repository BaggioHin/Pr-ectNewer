package com.example.demo.entity.gradeAndEvaluate;

import com.example.demo.constant.TypeGrade;
import com.example.demo.entity.classAndLearn.CourseClass;
import com.example.demo.entity.courseAndAcademic.Subject;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_class_id")
    @NotNull
    private CourseClass courseClass;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    @NotNull
    private Subject subject;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TypeGrade typeGrade;

    @NotNull
    private LocalDate examDate;

    @Min(1)
    private int duration;

    @OneToMany(mappedBy = "exam")
    private List<ExamResult> examResults;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamQuestion> questions = new ArrayList<>();
}

