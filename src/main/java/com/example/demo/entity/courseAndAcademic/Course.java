package com.example.demo.entity.courseAndAcademic;

import com.example.demo.constant.StatusCourse;
import com.example.demo.entity.classAndLearn.CourseClass;
import com.example.demo.entity.loginAndProcess.Enrollment;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Course {

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

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StatusCourse statusCourse;

    @Min(0)
    private Long credit;

    @OneToMany(mappedBy = "course")
    private List<CourseClass> classes = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private Set<Subject> subjects;
}
