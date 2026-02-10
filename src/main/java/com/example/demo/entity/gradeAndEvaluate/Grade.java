package com.example.demo.entity.gradeAndEvaluate;

import com.example.demo.constant.TypeGrade;
import com.example.demo.entity.classAndLearn.CourseClass;
import com.example.demo.entity.loginAndProcess.StudentProgress;
import com.example.demo.entity.people.Student;
import com.example.demo.entity.people.Teacher;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(0)
    private double score;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TypeGrade typeGrade;

    private String remark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @NotNull
    private Student student;

    @OneToMany(mappedBy = "grade")
    private List<ExamResult> examResultList;
}


