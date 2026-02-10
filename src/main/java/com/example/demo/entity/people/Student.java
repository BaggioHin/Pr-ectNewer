package com.example.demo.entity.people;

import com.example.demo.entity.authAndUser.User;
import com.example.demo.entity.classAndLearn.Attendance;
import com.example.demo.entity.gradeAndEvaluate.ExamResult;
import com.example.demo.entity.gradeAndEvaluate.Grade;
import com.example.demo.entity.loginAndProcess.Enrollment;
import com.example.demo.entity.loginAndProcess.StudentProgress;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    private Long userId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @NotBlank
    @Size(max = 50)
    @Column(unique = true, length = 50)
    private String studentCode;

    @OneToMany(mappedBy = "student")
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "student")
    private List<Grade> grades;

    @OneToMany(mappedBy = "student")
    private List<Attendance> attendances;

    @OneToMany(mappedBy = "student")
    private List<StudentProgress> studentProgresses;

//    @OneToMany(mappedBy = "student")
//    private List<StudentStatusHistory> studentStatusHistories;

    @OneToMany(mappedBy = "student")
    private List<ExamResult> examResults;
}


